
from Grad_LRS import *;

class GradientGoldenSearchSection(GradientDescending):
    def gradient_step(self, func, x: np.array, dir: np.array, learning_rate: float = 0.5, eps: float = 0.001, max_iterations: int = 10000) -> np.array:
        golden_ratio = (5 ** 0.5 - 1) / 2
        left = x
        right = x + dir * learning_rate
        l, r = (right - left) * (1 - golden_ratio) + left, \
               (right - left) * golden_ratio + left
        func_l = func(l)
        func_r = func(r)
        self.func_count+=2
        if func_l < func_r:
            right = r
            func_r = func_l
            saved_right = True
        else:
            left = l
            func_l = func_r
            saved_right = False


        for i in range(max_iterations):
            self.iter_count+=1
            if np.linalg.norm(right - left) < eps:
                return right
            l = (right - left) * (1 - golden_ratio) + left
            r = (right - left) * golden_ratio + left
            if saved_right:
                func_l = func(l)
            else:
                func_r = func(r)

            self.func_count += 1
            if func_l < func_r:
                right = r
                func_r = func_l
                saved_right = True
            else:
                left = l
                func_l = func_r
                saved_right = False
        return right

