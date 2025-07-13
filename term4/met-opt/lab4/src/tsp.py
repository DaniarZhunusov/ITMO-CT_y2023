import random
import matplotlib.pyplot as plt
import numpy as np

num_cities = 10
num_individuals = 100
num_parents = 50
mutation_rate = 0.2
generations = 200

distance_matrix = [
    [0, 3, 4, 2, 7, 5, 6, 8, 9, 1],
    [3, 0, 1, 4, 2, 7, 9, 6, 5, 8],
    [4, 1, 0, 5, 3, 6, 2, 9, 8, 7],
    [2, 4, 5, 0, 8, 9, 3, 7, 6, 1],
    [7, 2, 3, 8, 0, 4, 5, 1, 9, 6],
    [5, 7, 6, 9, 4, 0, 1, 3, 2, 8],
    [6, 9, 2, 3, 5, 1, 0, 4, 8, 7],
    [8, 6, 9, 7, 1, 3, 4, 0, 5, 2],
    [9, 5, 8, 6, 9, 2, 8, 5, 0, 1],
    [1, 8, 7, 1, 6, 8, 7, 2, 1, 0]
]

# 1. Генерация начальной популяции
def generate_population(num_cities, num_individuals):
    population = []
    for _ in range(num_individuals):
        individual = list(range(2, num_cities + 1))
        random.shuffle(individual)
        individual = [1] + individual + [1]  # Начинаем и заканчиваем в городе 1
        population.append(individual)
    return population

# 2. Функция приспособленности (расстояние маршрута)
def calculate_fitness(individual, distance_matrix):
    total_distance = 0
    for i in range(len(individual) - 1):
        total_distance += distance_matrix[individual[i] - 1][individual[i + 1] - 1]
    return total_distance

# 3. Отбор родителей (селекция)
def selection(population, num_parents):
    parents = []
    for _ in range(num_parents):
        tournament = random.sample(population, 5) 
        best = min(tournament, key=lambda x: calculate_fitness(x, distance_matrix))
        parents.append(best)
    return parents

# 4. Скрещивание 
def crossover(parent1, parent2):
    size = len(parent1)
    start, end = sorted(random.sample(range(1, size - 1), 2))
    child = [-1] * size
    child[start:end] = parent1[start:end]
    
    current_pos = 1
    for city in parent2[1:-1]:
        if city not in child[start:end]:
            while current_pos < size - 1 and child[current_pos] != -1:
                current_pos += 1
            if current_pos >= size - 1:
                break
            child[current_pos] = city
            current_pos += 1
    
    child[0] = child[-1] = 1
    return child

# 5. Мутация 
def mutation(individual, mutation_rate):
    if random.random() < mutation_rate:
        idx1, idx2 = random.sample(range(1, len(individual) - 1), 2)
        individual[idx1], individual[idx2] = individual[idx2], individual[idx1]
    return individual

# 6. Замена популяции 
def replace_population(old_population, offspring, elite_size=10):
    combined = old_population + offspring
    combined.sort(key=lambda x: calculate_fitness(x, distance_matrix))
    return combined[:len(old_population)]

# Основной цикл алгоритма
population = generate_population(num_cities, num_individuals)
best_fitness_history = []
avg_fitness_history = []

for generation in range(generations):
    # Отбор
    parents = selection(population, num_parents)
    
    # Скрещивание
    offspring = []
    for i in range(0, len(parents), 2):
        child1 = crossover(parents[i], parents[i + 1])
        child2 = crossover(parents[i + 1], parents[i])
        offspring.extend([child1, child2])
    
    # Мутация
    offspring = [mutation(child, mutation_rate) for child in offspring]
    
    # Замена популяции
    population = replace_population(population, offspring)
    
    # Сбор статистики
    fitness_values = [calculate_fitness(ind, distance_matrix) for ind in population]
    best_fitness = min(fitness_values)
    avg_fitness = sum(fitness_values) / len(fitness_values)
    best_fitness_history.append(best_fitness)
    avg_fitness_history.append(avg_fitness)
    
    print(f"Generation {generation + 1}: Best = {best_fitness}, Avg = {avg_fitness:.2f}")

# Результаты
best_route = min(population, key=lambda x: calculate_fitness(x, distance_matrix))
best_distance = calculate_fitness(best_route, distance_matrix)
print("\nЛучший маршрут:", best_route)
print("Длина маршрута:", best_distance)

plt.figure(figsize=(12, 5))
plt.subplot(1, 2, 1)
plt.plot(best_fitness_history, label='Лучший', color='green')
plt.plot(avg_fitness_history, label='Средний', color='blue')
plt.xlabel('Поколение')
plt.ylabel('Длина маршрута')
plt.title('Сходимость алгоритма')
plt.legend()
plt.grid()

plt.subplot(1, 2, 2)
cities = np.random.rand(num_cities, 2) * 100
for i, (x, y) in enumerate(cities):
    plt.scatter(x, y, color='red')
    plt.text(x + 1, y + 1, str(i + 1), fontsize=10)

for i in range(len(best_route) - 1):
    city1 = best_route[i] - 1
    city2 = best_route[i + 1] - 1
    plt.plot([cities[city1][0], cities[city2][0]], 
             [cities[city1][1], cities[city2][1]], 'b-')

plt.title('Лучший маршрут')
plt.xlabel('X')
plt.ylabel('Y')
plt.tight_layout()
plt.show()
