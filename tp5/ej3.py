import matplotlib.pyplot as plt
from statistics import stdev


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

  # Commented version skips some deviation lines so you can see the average line (only for debugging purposes)
  # y_std = [stdev(y_time)*(i%100 == 0) for i, y_time in enumerate(zip(*y_for_r))]
  sick_std = [stdev(sick_time) for sick_time in zip(*sick_for_static)]
  inmune_std = [stdev(inmune_time) for inmune_time in zip(*inmune_for_static)]
  
  sicks_avg.append(sick_avg)
  sicks_std.append(sick_std)
  inmunes_avg.append(inmune_avg)
  inmunes_std.append(inmune_std)

for sick_avg, inmune_avg, static in zip(sicks_avg, inmunes_avg, static_percentage):
  x = [step*1e-2 for step in range(len(sick_avg))]  # step*dt = tiempo (s)
  fig = plt.figure(figsize=(12,7))
  ax1 = fig.add_subplot(111)
  ax1.set_xlabel('Tiempo (s)', fontsize=27)
  ax1.set_ylabel(f'Distribución con {int(static*100)}% inmoviles', fontsize=27)
  ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
  ax1.stackplot(x, sick_avg, inmune_avg, [1.0-sick-inmune for sick, inmune in zip(sick_avg, inmune_avg)], labels=['Enfermos', 'Curados', 'Sanos'], colors=['tab:green', 'tab:blue', 'tab:orange'])
  ax1.margins(0, 0) # Set margins to avoid "whitespace"
  plt.legend(loc='upper left', fontsize=18)
  plt.grid()
  p = '{0:.2f}'.format(static).replace('.', ',')
  plt.savefig(f'ej3_static-{p}')

