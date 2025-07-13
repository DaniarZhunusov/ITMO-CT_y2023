from ucimlrepo import fetch_ucirepo

from collections.abc import Callable
import numpy as np
import pandas as pd
import math
import random
from keras import models, layers, optimizers
import torch
import torch.optim as optim
from torch.profiler import profile, record_function, ProfilerActivity



class Schedulers:
  @staticmethod
  def fixed(initial: float) -> float:
    return lambda iteration: initial

  @staticmethod
  def decay(initial: float, decay_speed: float) -> float:
    return lambda iteration: initial / (1 + iteration * decay_speed)

  @staticmethod
  def sqrt_decay(initial: float, decay_speed: float) -> float:
    return lambda iteration: initial / np.sqrt(1 + iteration * decay_speed)

  @staticmethod
  def exponential(initial: float, exponentiation_speed: float) -> float:
    return lambda iteration: initial * np.power(exponentiation_speed, iteration)

  @staticmethod
  def reset_exp(initial: float, exponentiation_speed: float, reset: int) -> float:
    return lambda iteration: initial * np.power(exponentiation_speed, iteration % reset)


class LossFunctions:
  @staticmethod
  def L1(y_calc: np.ndarray, y: np.ndarray) -> np.ndarray:
    return np.abs(y_calc - y)

  @staticmethod
  def L2(y_calc: np.ndarray, y: np.ndarray) -> np.ndarray:
    return (y_calc - y)**2


class Regularizers:
  @staticmethod
  def mean(loss_function: Callable[[float, float], float]) -> Callable[[np.ndarray, np.ndarray, np.ndarray], float]:
    def ret(batch_calc: np.ndarray,
            batch: np.ndarray,
            weights: np.ndarray) -> float:
      return np.sum(loss_function(batch_calc, batch) / batch.size)
    return ret

  @staticmethod
  def elastic(l1_weight: float, l2_weight: float) -> Callable[[np.ndarray, np.ndarray, np.ndarray], float]:
    def ret(batch_calc: np.ndarray,
            batch: np.ndarray,
            weights: np.ndarray) -> float:
      return Regularizers.mean(LossFunctions.L2)(batch_calc, batch, weights) +\
             l1_weight * np.sum(np.abs(weights)) + l2_weight * np.sum(weights ** 2)
    return ret



class StochasticGradientDescent:
  def __init__(self,
               input_size: int,
               polynomial_degree: int,
               batch_size: int,
               regularizer: Callable[[np.ndarray, np.ndarray, np.ndarray], float] = Regularizers.mean(LossFunctions.L1),
               scheduler: Callable[[int], float] = Schedulers.fixed(0.01),
               max_iterations: int = 1000,
               grad_clip: float = None,
               eps: float = 1e-8):
    self.input_size = input_size
    self.polynomial_degree = polynomial_degree
    self.batch_size = batch_size
    self.regularizer = regularizer
    self.scheduler = scheduler
    self.max_iterations = max_iterations
    self.grad_clip = grad_clip
    self.eps = eps
    self.polynomials = self.get_all_polynomials()
    self.initial_weights = np.zeros(math.comb(input_size + polynomial_degree, polynomial_degree))

  def get_all_polynomials(self):
    answer = []
    def rec_pol(m, n, curr, result):
      if curr == m - 1:
        for i in range(n + 1):
            answer.append(result + [i])
      else:
        for i in range(n + 1):
          rec_pol(m, n - i, curr + 1, result + [i])

    rec_pol(self.input_size, self.polynomial_degree, 0, [])
    return answer

  def compute_polynomial(self, weights: np.ndarray, input_point: np.ndarray) -> float:
    return np.sum(self.get_polynomial(weights, input_point))

  def get_polynomial(self, weights: np.ndarray, input_point: np.ndarray) -> np.ndarray:
    ans = np.zeros(weights.size)
    for i in range(weights.size):
      ans[i] = weights[i]
      for j in range(input_point.size):
        ans[i] *= input_point[j] ** self.polynomials[i][j]
    return ans

  def compute_polynomials(self, weights: np.ndarray, input_points: pd.DataFrame) -> np.ndarray:
    return np.sum(self.get_polynomials(weights, input_points), axis=1)

  def get_polynomials(self, weights: np.ndarray, input_points: pd.DataFrame) -> np.ndarray:
    answer = []
    for index, row in input_points.iterrows():
      answer.append(self.get_polynomial(weights, row.values))
    return np.array(answer)

  def compute_regularizer(self, weights: np.ndarray, input_points: pd.DataFrame, output_points: pd.DataFrame) -> float:
    return self.regularizer(self.compute_polynomials(weights, input_points), output_points.to_numpy().T[0], weights)

  def grad(self, weights: np.ndarray, input_points: pd.DataFrame, output_points: pd.DataFrame, h: float = 1e-6) -> np.ndarray:
    polynomials = self.get_polynomials(weights, input_points)
    raw = self.get_polynomials(np.ones(weights.size), input_points)
    sums = np.sum(polynomials, axis=1)
    answer = np.zeros(weights.size)
    for dim in range(weights.size):
      original = weights[dim]

      weights[dim] = original + h
      new_sums = np.zeros(self.batch_size)
      for i in range(self.batch_size):
        new_sums[i] = sums[i] + raw[i][dim] * h
      high = self.regularizer(new_sums, output_points.to_numpy().T[0], weights)

      weights[dim] = original - h
      for i in range(self.batch_size):
        new_sums[i] = sums[i] - raw[i][dim] * h
      low = self.regularizer(new_sums, output_points.to_numpy().T[0], weights)

      weights[dim] = original
      answer[dim] = (high - low) / (2 * h)
    return answer

  def get_step(self, grad: np.ndarray, iteration: int) -> np.ndarray:
    return -self.scheduler(iteration) * grad

  def run_init(self):
    pass

  def weights_transform(self, weights: np.ndarray) -> np.ndarray:
    return weights

  def run(self, input_data: pd.DataFrame, output_data: pd.DataFrame, debug: bool = True) -> np.ndarray:
    weights = self.initial_weights
    self.run_init()
    if debug:
      print("SGD Running")
    percent = -1
    for iteration in range(self.max_iterations):
      if debug and iteration % 10000 == 0:
        print(f"\nLoss: {self.regularizer(self.compute_polynomials(weights, input_data), output_data.to_numpy().T[0], weights)}")
      if debug and int(iteration / self.max_iterations * 100) > percent:
        percent = int(iteration / self.max_iterations * 100)
        print(f"\r{percent}% done", end="")

      input_batch = input_data.sample(min(len(input_data), self.batch_size))
      output_batch = output_data.loc[input_batch.index]
      grad = self.grad(self.weights_transform(weights), input_batch, output_batch)
      if not (self.grad_clip is None):
        grad = np.clip(grad, -self.grad_clip, self.grad_clip)
      new_weights = weights + self.get_step(grad, iteration)
      if np.linalg.norm(new_weights - weights) < self.eps:
        break
      weights = new_weights
    if debug:
      print(f"\rSGD Done")
    return weights


class MomentumSGD(StochasticGradientDescent):
  def __init__(self,
               momentum: float,
               input_size: int,
               polynomial_degree: int,
               batch_size: int,
               regularizer: Callable[[np.ndarray, np.ndarray, np.ndarray], float] = Regularizers.mean(LossFunctions.L1),
               scheduler: Callable[[int], float] = Schedulers.fixed(0.01),
               max_iterations: int = 1000,
               grad_clip: float = None,
               eps: float = 1e-8):
    super().__init__(input_size, polynomial_degree, batch_size, regularizer, scheduler, max_iterations, grad_clip, eps)
    self.momentum = momentum

  def get_step(self, grad: np.ndarray, iteration: int) -> np.ndarray:
    self.last_step = self.last_step * self.momentum - self.scheduler(iteration) * grad
    return self.last_step

  def run_init(self):
    self.last_step = np.zeros(self.input_size)


class NesterovSGD(MomentumSGD):
  def weights_transform(self, weights: np.ndarray) -> np.ndarray:
    return weights + self.momentum * self.last_step