from Grad_LRS import *
import numpy as np

def bfgs_method(init: np.ndarray, gd, f, start_beta=1.0, tol=1e-10, max_iter=100, backtrack=False, back_coef=0.8):
    x = np.copy(init)
    ones = np.eye(x.shape[0])
    B = ones * (1 / start_beta)
    res = [x]
    gd.iter_count = 0
    gd.grad_count = 0
    gd.hes_count = 0
    for i in range(max_iter):
        grad_x = gd.grad(f, x)
        sk = -np.dot(B, grad_x)

        if not backtrack:
            x_next = gd.gradient_step(func=f, x=x, dir=sk)
        else:
            alpha = backtracking(x, grad_x, gd, f, sk, q=back_coef)
            x_next = x + alpha * sk
        
        res.append(x_next)
        grad_x_next = gd.grad(f, x_next)
        yk = grad_x_next - grad_x
        gd.grad_count += 2
        if abs(np.dot(yk, sk)) < tol:
            break
        pk = 1 / np.dot(yk, sk)
        x = x_next
        if np.linalg.norm(sk) < tol:
            break
        B = np.dot(np.dot(ones - pk * np.outer(sk, yk), B), ones - pk * np.outer(yk, sk)) + pk * np.dot(sk, sk)
        gd.iter_count += 1
    return np.array(res), f(x), gd.iter_count, gd.grad_count, gd.hes_count

def backtracking(x, grad_x, gd, f, sk, c1=1e-4, alpha=1.0, q=0.8, max_iter=100):
    fx = f(x)
    for i in range(max_iter):
        x_next = x + alpha * sk
        gd.func_count += 1
        if f(x_next) <= fx + c1 * alpha * np.dot(grad_x, sk):
            return alpha
        alpha *= q
    return alpha
        
