import numpy as np
import random
import math
from matplotlib import pyplot as plt

class GeneticAlgorithm:
    def __init__(self, objective_func, bounds, population_size=50, 
                 generations=100, crossover_rate=0.8, mutation_rate=0.1,
                 selection_strategy='tournament', elitism=True):
        self.objective_func = objective_func
        self.bounds = np.array(bounds)
        self.pop_size = population_size
        self.generations = generations
        self.cross_rate = crossover_rate
        self.mut_rate = mutation_rate
        self.selection = selection_strategy
        self.elitism = elitism
        self.history = []
        
    def initialize_population(self):
        """Инициализация случайной популяции в заданных границах"""
        return np.array([self.bounds[:, 0] + 
                       np.random.rand(len(self.bounds)) * 
                       (self.bounds[:, 1] - self.bounds[:, 0]) 
                       for _ in range(self.pop_size)])
    
    def evaluate(self, population):
        """Вычисление значений целевой функции"""
        return np.array([self.objective_func(ind) for ind in population])
    
    def tournament_selection(self, population, fitness, k=3):
        """Турнирная селекция с размером турнира k"""
        selected = []
        for _ in range(self.pop_size):
            candidates = np.random.choice(self.pop_size, size=k)
            winner = candidates[np.argmin(fitness[candidates])]
            selected.append(population[winner])
        return np.array(selected)
    
    def roulette_selection(self, population, fitness):
        """Селекция методом рулетки (для минимизации)"""
        max_fit = np.max(fitness)
        inverted_fitness = max_fit - fitness + 1e-6 
        probs = inverted_fitness / np.sum(inverted_fitness)
        selected_indices = np.random.choice(self.pop_size, size=self.pop_size, p=probs)
        return population[selected_indices]
    
    def crossover(self, parent1, parent2):
        """Арифметическое скрещивание (BLX-α)"""
        alpha = np.random.rand()
        child1 = alpha * parent1 + (1 - alpha) * parent2
        child2 = alpha * parent2 + (1 - alpha) * parent1
        return child1, child2
    
    def mutate(self, individual, generation, max_generations):
        """Адаптивная мутация с уменьшением силы мутации со временем"""
        mutation_power = (self.bounds[:, 1] - self.bounds[:, 0]) * (1 - generation/max_generations) / 10
        mutated = individual + mutation_power * np.random.randn(len(self.bounds))
        return np.clip(mutated, self.bounds[:, 0], self.bounds[:, 1])
    
    def run(self):
        """Основной цикл генетического алгоритма"""
        population = self.initialize_population()
        fitness = self.evaluate(population)
        
        best_idx = np.argmin(fitness)
        best_individual = population[best_idx].copy()
        best_fitness = fitness[best_idx]
        self.history.append(best_fitness)
        
        for gen in range(self.generations):
            # Селекция
            if self.selection == 'tournament':
                selected = self.tournament_selection(population, fitness)
            else:
                selected = self.roulette_selection(population, fitness)
            
            # Скрещивание
            offspring = []
            for i in range(0, self.pop_size, 2):
                parent1, parent2 = selected[i], selected[i+1]
                if random.random() < self.cross_rate:
                    child1, child2 = self.crossover(parent1, parent2)
                else:
                    child1, child2 = parent1.copy(), parent2.copy()
                offspring.extend([child1, child2])
            offspring = np.array(offspring[:self.pop_size])
            
            # Мутация
            for i in range(self.pop_size):
                if random.random() < self.mut_rate:
                    offspring[i] = self.mutate(offspring[i], gen, self.generations)
            
            # Сохраняем лучшую особь
            if self.elitism:
                offspring_fitness = self.evaluate(offspring)
                worst_idx = np.argmax(offspring_fitness)
                offspring[worst_idx] = best_individual
                offspring_fitness[worst_idx] = best_fitness
            else:
                offspring_fitness = self.evaluate(offspring)
            
            # Обновление популяции
            population = offspring
            fitness = offspring_fitness
            
            # Обновление лучшего решения
            current_best_idx = np.argmin(fitness)
            if fitness[current_best_idx] < best_fitness:
                best_individual = population[current_best_idx].copy()
                best_fitness = fitness[current_best_idx]
            
            self.history.append(best_fitness)
            
            if gen % 10 == 0:
                print(f"Generation {gen}: Best Fitness = {best_fitness:.6f}")
        
        return best_individual, best_fitness, self.history

# Тестовые функции
def himmelblau(x):
    """Функция Химмельблау"""
    return (x[0]**2 + x[1] - 11)**2 + (x[0] + x[1]**2 - 7)**2

def rosenbrock(x):
    """Функция Розенброка"""
    return 100*(x[1] - x[0]**2)**2 + (1 - x[0])**2

def rastrigin(x, A=10):
    """Функция Растригина"""
    return A*len(x) + sum([(xi**2 - A*np.cos(2*math.pi*xi)) for xi in x])

if __name__ == "__main__":
    # Настройки для разных функций
    test_cases = [
        ("Himmelblau", himmelblau, [(-5, 5), (-5, 5)]),
        ("Rosenbrock", rosenbrock, [(-5, 5), (-5, 5)]),
        ("Rastrigin", rastrigin, [(-5.12, 5.12), (-5.12, 5.12)])
    ]
    
    # Общие параметры алгоритма
    params = {
        'population_size': 50,
        'generations': 200,
        'crossover_rate': 0.85,
        'mutation_rate': 0.15,
        'selection_strategy': 'tournament',
        'elitism': True
    }
    
    # Запуск оптимизации для каждой функции
    for name, func, bounds in test_cases:
        print(f"\n=== Оптимизация функции {name} ===")
        
        ga = GeneticAlgorithm(func, bounds, **params)
        solution, value, history = ga.run()
        
        print(f"\nРезультаты для функции {name}:")
        print(f"Лучшее решение: {np.round(solution, 4)}")
        print(f"Значение функции: {value:.6f}")
        print(f"Количество поколений: {params['generations']}")
        print(f"Всего вычислений функции: {params['population_size'] * params['generations']}")
        
        plt.figure(figsize=(10, 5))
        plt.plot(history)
        plt.title(f'Сходимость генетического алгоритма ({name})')
        plt.xlabel('Поколение')
        plt.ylabel('Лучшее значение функции')
        plt.grid()
        plt.show()
