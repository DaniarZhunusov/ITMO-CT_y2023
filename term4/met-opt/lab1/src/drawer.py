import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import minimize

history = np.empty([0, 2])
is_opt = False

def add_history(inter):
    global history
    history = np.vstack((history, inter))

def plot(func, results_list, grid, label):
    if not isinstance(results_list, list):
        results_list = [results_list]

    x, y = np.meshgrid(np.linspace(grid[0], grid[1], 200), np.linspace(grid[0], grid[1], 200))

    z = np.zeros_like(x)
    for i in range(x.shape[0]):
        for j in range(x.shape[1]):
            z[i, j] = func((x[i, j], y[i, j]))

    plt.figure()
    plt.contour(x, y, z, levels=30)

    if not is_opt:
        for res in results_list:
            plt.plot(res[:, 0], res[:, 1], 'o-', color='red')
            plt.plot(res[-1, 0], res[-1, 1], 'x', markersize=10, color='red', label=label)
    else:
        for res in results_list:
            plt.plot(res.x[0], res.x[1], 'x', markersize=10, color='blue')
        plt.plot(history[:, 0], history[:, 1], label=label, marker='o', color='blue')


    plt.xlim(grid)
    plt.ylim(grid)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.legend()
    plt.grid(True)
    plt.show()


def print_output(init, gd, func, label, grid):
    global is_opt
    is_opt = False
    
    res, func_min, iter_count, func_count, grad_count = gd(func, init)

    print()
    print(label)
    print()
    print(f"Total iterations count: {iter_count}")
    print(f"Objective function calls: {func_count}")
    print(f"Gradient computations: {grad_count}")
    print(f"Result Minimum:{func_min}")

    plot(func, res, grid, label)


def print_output_opt(init, func, method, grid):
    global history, is_opt
    history = init
    is_opt = True
    
    res = minimize(func, init, method=method, callback=add_history)
    print(f"\n{method}")
    print(f"Total iterations count: {res.nit}")
    print(f"Objective function calls: {res.nfev}")
    if hasattr(res, 'njev'):
        print(f"Gradient computations: {res.njev}")
    print(f"Result Minimum: {res.fun}")
    plot(func, res, grid, method)
