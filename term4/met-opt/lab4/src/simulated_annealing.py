import math
import random
import numpy as np
from scipy.optimize import minimize


def simulated_annealing(objective_func, initial_solution, temperature, cooling_rate, min_temperature, max_iterations):
    current_solution = initial_solution
    current_value = objective_func(current_solution)

    functions_calls = 1;

    best_solution = current_solution
    best_value = current_value

    iteration = 0

    while temperature > min_temperature and iteration < max_iterations:
        # Генерируем соседнее решение
        neighbor_solution = current_solution + np.random.uniform(-1, 1, len(initial_solution))
        neighbor_value = objective_func(neighbor_solution)
        functions_calls+=1

        # Разница между текущим и соседним решением
        delta = neighbor_value - current_value

        # Если соседнее решение лучше, принимаем его
        if delta < 0:
            current_solution = neighbor_solution
            current_value = neighbor_value

            # Если это лучшее решение за все время, сохраняем его
            if neighbor_value < best_value:
                best_solution = neighbor_solution
                best_value = neighbor_value
        # Если соседнее решение хуже, принимаем его с некоторой вероятностью
        else:
            probability = math.exp(-delta / temperature)
            if random.random() < probability:
                current_solution = neighbor_solution
                current_value = neighbor_value

        # Охлаждаем температуру
        temperature *= cooling_rate
        iteration += 1

    return best_solution, best_value, iteration, functions_calls


if __name__ == "__main__":

    def himmelblau(x):
        """Функция Химмельблау"""
        return (x[0] ** 2 + x[1] - 11) ** 2 + (x[0] + x[1] ** 2 - 7) ** 2


    def rosenbrock(x):
        """Функция Розенброка"""
        return 100 * (x[1] - x[0] ** 2) ** 2 + (1 - x[0]) ** 2


    def rastrigin(x, A=10):
        """Функция Растригина"""
        n = len(x)
        return A * n + sum([(xi ** 2 - A * np.cos(2 * math.pi * xi)) for xi in x])

    
    func = himmelblau

    # Параметры алгоритма
    initial_solution = np.array([-2, 2])  # Начальная точка
    initial_temperature = 1000.0
    cooling_rate = 0.95
    min_temperature = 1e-7
    max_iterations = 10000

    best_solution, best_value, iteration, functions_calls = simulated_annealing(
        func, initial_solution, initial_temperature,
        cooling_rate, min_temperature, max_iterations
    )

    print(f"Лучшее решение: {best_solution}")
    print(f"Значение функции: {best_value}")
    print(f"Количество итераций: {iteration}")
    print(f"Число вычислений функции: {functions_calls}\n")

    initial_guess = np.array([-2, 2])

    result_bfgs = minimize(func, initial_guess, method='BFGS')

    print("Результат BFGS (SciPy):")
    print(f"Оптимальная точка: {result_bfgs.x}")
    print(f"Значение функции: {result_bfgs.fun}")
    print(f"Количество итераций: {result_bfgs.nit}")
    print(f"Число вычислений функции: {result_bfgs.nfev}\n")

    result_l_bfgs_b = minimize(func, initial_guess, method='L-BFGS-B', bounds=[(-5, 5), (-5, 5)])

    print("Результат L-BFGS-B (SciPy):")
    print(f"Оптимальная точка: {result_l_bfgs_b.x}")
    print(f"Значение функции: {result_l_bfgs_b.fun}")
    print(f"Количество итераций: {result_l_bfgs_b.nit}")
    print(f"Число вычислений функции: {result_l_bfgs_b.nfev}\n")

    result_cg = minimize(func, initial_guess, method='CG')

    print("Результат CG (SciPy):")
    print(f"Оптимальная точка: {result_cg.x}")
    print(f"Значение функции: {result_cg.fun}")
    print(f"Количество итераций: {result_cg.nit}")
    print(f"Число вычислений функции: {result_cg.nfev}\n")
