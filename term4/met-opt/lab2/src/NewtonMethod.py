from Grad_LRS import *
import numpy as np

# from drawer import *
from drawer import print_output_opt


def newton_method(init: np.array, gd, f, mode="newton_raphson", a=1.0, tol=1e-10, max_iter=100, eps=1e-6, verbose=False):
    x = np.copy(init)
    res = [x]
    gd.iter_count = 0
    gd.grad_count = 0
    gd.hes_count = 0

    for i in range(max_iter):
        gd.iter_count += 1

        grad = gd.grad(f, x)
        gd.grad_count += 1

        hess = gd.hessian(f, x)
        gd.hes_count += 1


        # Проверки только на значение чградиента = 0 часто оказываетс не оптимальной и при очень маленьком
        # значение tol не всегда сильно увеличивет точность минимума
        # if np.linalg.norm(grad) < tol:
        #          break

        try:
            direction = -np.linalg.solve(hess, grad)
        except np.linalg.LinAlgError:
            print("Гессиан вырожден. Прекращаем.")
            break

        if mode == "newton_raphson":
            f_line = lambda alpha: f(x + alpha[0] * direction)
            alpha_result, f_res, iter_count, func_count, grad_count = gd.find_min(func=f_line, init=np.array([0.0]))
            step_size = alpha_result[-1]
        else:
            step_size = a

        x_new = x + step_size * direction
        res.append(x_new)

        # Но тут мы используем 2 вызовов функции
        gd.func_count+=2
        if abs(f(x_new) - f(x)) < tol:
             break

        x = x_new

    return np.array(res), f(x), gd.iter_count, gd.grad_count, gd.hes_count

def newton_method_cg(init: np.array, grad, func, method="Newton-CG", grid=None):
    jacobian = lambda x: grad(func, x)
    print_output_opt(init, func, method, jac=jacobian, grid=np.array([-6, 6]))

def bfgs(init: np.array, func, method="BFGS"):
    print_output_opt(init, func, method, grid=np.array([-6, 6]))

def l_bfgs_b(init: np.array, func, method="L-BFGS-B"):
    print_output_opt(init, func, method, grid=np.array([-6, 6]))
