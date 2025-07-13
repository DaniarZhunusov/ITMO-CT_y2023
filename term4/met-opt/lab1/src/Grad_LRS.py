import numpy as np


class GradientDescending:

    def __init__(self):
        self.step_strategy = "exponential"  # default strategy
        self.grad_count = 0
        self.iter_count = 0
        self.func_count = 0

    @staticmethod
    def grad(f, x: np.array, h=1e-5) -> np.array:
        return (f(x[:, np.newaxis] + h * np.eye(x.size)) -
                f(x[:, np.newaxis] - h * np.eye(x.size))) / (2 * h)

    def gradient_step(self, func, x: np.array, dir: np.array, learning_rate: float = 0.5, eps: float = 0.001, max_iterations: int = 10000) -> np.array:
        self.iter_count +=1
        lr = learning_rate
        if self.step_strategy == "fixed":
            lr = learning_rate
        elif self.step_strategy == "decay":
            lr = learning_rate / (1 + self.iter_count * 0.01)
        elif self.step_strategy == "sqrt_decay":
            lr = learning_rate / np.sqrt(1 + self.iter_count)
        elif self.step_strategy == "exponential":
            lr = learning_rate * (0.95 ** self.iter_count)

        return x + dir * lr

    #def find_min(self, func, init: np.array, learning_rate: float = 0.2, eps: float = 0.001, max_iterations: int = 10000, noise_scale: float = 0.1)
    def find_min(self, func, init: np.array, learning_rate: float = 0.01, eps: float = 0.01,
                 max_iterations: int = 10000, noise_scale: float = 0.1) -> tuple[np.array, int, int, int, int]:
        result_min = [init]
        pos = init.copy()

        for i in range(max_iterations):
            direction = self.grad(func, pos)
            self.grad_count += 1
            self.func_count += 2

            #шум
            direction += np.random.normal(0, noise_scale, direction.shape)
            direction = -direction

            next_pos = self.gradient_step(func, pos, direction, learning_rate, eps, max_iterations)
            result_min.append(next_pos)

            if np.linalg.norm(next_pos - pos) < eps:
                return np.array(result_min), func(result_min[-1]), self.iter_count, self.func_count, self.grad_count
            pos = next_pos


        return np.array(result_min), func(result_min[-1]), self.iter_count, self.func_count, self.grad_count
