import matplotlib.pyplot as plt
from statistics import stdev
import sys

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)


sicks_avg = []
sicks_std = []
inmunes_avg = []
inmunes_std = []
static_percentage = [i/10 for i in range(11)]
for p in static_percentage:
  p = '{0:.2f}'.format(p).replace('.', ',')
  sick_for_static = []
  inmune_for_static = []
  for seed in range(10):
    with open(f'distribution_static-{p}_seed-{seed}.csv') as f:
      lines = f.readlines()
      sick = [float(line.split(' ')[0]) for line in lines]
      inmune = [float(line.split(' ')[1]) for line in lines]
      sick_for_static.append(sick)
      inmune_for_static.append(inmune)
  sick_avg = [avg(static_time) for static_time in zip(*sick_for_static)]
  inmune_avg = [avg(inmune_time) for inmune_time in zip(*inmune_for_static)]

  sick_std = [stdev(sick_time) for sick_time in zip(*sick_for_static)]
  inmune_std = [stdev(inmune_time) for inmune_time in zip(*inmune_for_static)]
  
  sicks_avg.append(sick_avg)
  sicks_std.append(sick_std)
  inmunes_avg.append(inmune_avg)
  inmunes_std.append(inmune_std)

sicks_at_25_avg = [sick[2500] for sick in sicks_avg]
sicks_at_25_std = [sick[2500] for sick in sicks_std]
inmunes_at_25_avg = [inmune[2500] for inmune in inmunes_avg]
inmunes_at_25_std = [inmune[2500] for inmune in inmunes_std]
already_infected_at_25_avg = [sick+cured for sick, cured in zip(sicks_at_25_avg, inmunes_at_25_avg)]
already_infected_at_25_std = [sick+cured for sick, cured in zip(sicks_at_25_std, inmunes_at_25_std)]

fig = plt.figure(figsize=(12, 7))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Porcentaje de partículas estáticas (%)', fontsize=27)
ax1.set_ylabel(f'Partículas ya infectadas (%)', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
ax1.errorbar(static_percentage, already_infected_at_25_avg, fmt='o', yerr=already_infected_at_25_std)
# plt.legend(loc='upper left', fontsize=18)
plt.grid()
plt.show()
# plt.savefig(f'ej3_IO')
print(len(static_percentage), len(already_infected_at_25_avg), len(already_infected_at_25_std))
print(static_percentage, already_infected_at_25_avg, already_infected_at_25_std)


