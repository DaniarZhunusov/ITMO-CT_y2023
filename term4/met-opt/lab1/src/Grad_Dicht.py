
from Grad_LRS import  *


class DichtGradientDescending(GradientDescending):

    def gradient_step(self, func, x: np.array, dir: np.array, learning_rate: float = 0.5, eps: float = 0.001, max_iterations: int = 10000) -> np.array:
        h = 1e-5
        left = x
        right = x + dir * learning_rate
        for i in range(max_iterations):
            self.iter_count+=1
            if np.linalg.norm(left - right) < eps:
                return left
            middle = (left + right) / 2
            step = dir * h
            derive =  (func(x + step) - func(x - step)) / 2
            self.func_count +=2
            if derive < 0:
                left = middle
            else:
                right = middle
        return left

