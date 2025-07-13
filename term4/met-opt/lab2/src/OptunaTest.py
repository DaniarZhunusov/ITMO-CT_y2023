from examples import *

import optuna
import numpy as np
from Grad_LRS import GradientDescending


# для тестирование градиетных спусков
def objective_gd(trial):
    gd = GradientDescending()
    gd.step_strategy = trial.suggest_categorical("step_strategy", ["fixed", "decay", "sqrt_decay", "exponential"])
    learning_rate = trial.suggest_float("learning_rate", 0.001, 1.0)
    eps = trial.suggest_float("eps", 1e-5, 1e-1)
    noise_scale = trial.suggest_float("noise_scale", 0.0, 0.1)

    def f(x):
        return (x[0] - 2) ** 2 + (x[1] + 3) ** 2

    init = np.array([10.0, -10.0])
    result_path, f_val, iters, func_calls, grad_calls = gd.find_min(
        f, init=init, learning_rate=learning_rate, eps=eps,
        max_iterations=1000, noise_scale=noise_scale
    )

    return f_val


# для тестирование метода ньютона
def objective_newton(trial):
    tol = trial.suggest_float("tol", 1e-8, 1e-3)
    max_iter = trial.suggest_int("max_iter", 10, 100)

    #задаешь gd для оптимизированого Ньютона — Рафсона
    gd = GradientDescending()


    def f(x):
        return (x[0] - 3) ** 2 + (x[1] + 1) ** 2

    init = np.array([5.0, -5.0])

    x_opt, f_val, iter_count, grad_calls, hess_calls = newton_method(
        init=init,
        gd=gd,
        f=f,
        tol=tol,
        max_iter=max_iter
    )
    return f_val


study = optuna.create_study(direction="minimize")
study.optimize(objective_newton, n_trials=50) #здесь меняем на что нужно/хочешь прогнать по пaраметром + количесво итераций n_trials

print("Лучшие параметры:")
print(study.best_params)
print("Лучшее значение функции:", study.best_value)
