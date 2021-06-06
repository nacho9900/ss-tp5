import matplotlib.pyplot as plt
from statistics import stdev
import math

def d_string(d):
    return str(d).replace('.', '_')

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100_caudal.png'

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

def calc_caudal(N, d):
    list_of_times = []
    for i in range(10):
        # with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        with open(f'ej_c/flowPerOpening_N_{N}_opening_{d_string(d)}_dt_1e-2_seed_{i}.csv') as f:
            lines = f.readlines()
            list_of_times.append([float(line.split(' ')[0]) for line in lines])
    
    # Set up data
    x = [avg(times) for times in zip(*list_of_times)]
    # x_err = [stdev(times) for times in zip(*list_of_times)]
    y = [i for i in range(N)]

    i1 = int(len(x) * 0.4)
    i2 = int(len(x) * 0.6)
    caudal = (y[i2] - y[i1]) / (x[i2] - x[i1])
    return caudal

def calc_beverloo(N, d):
    density = 3.2
    force = 320
    beverloo = density * math.sqrt(force / 80) * (d - 0.3)**1.5
    return beverloo

caudales = []
beverloos = []
confs = [(200, 1.2), (260, 1.8), (320, 2.4), (380, 3.0)]
for N, d in confs:
    caudales.append(calc_caudal(N, d))
    beverloos.append(calc_beverloo(N, d))
# print([c1-c2 for c2, c1 in zip(caudales[:-1], caudales[1:])])

# Init graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Apertura de salida (m)', fontsize=27)
ax1.set_ylabel('Caudal', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)

# Plot
ds = [conf[1] for conf in confs]
ax1.errorbar(ds, caudales, yerr=stdev(caudales), fmt='-o', label='Caudales simulacion')
ax1.errorbar(ds, [b + 0 for b in beverloos], fmt='-o', label='Caudales beverloo')
fig1=plt.gcf()
ax1.legend(fontsize=27)

# Descomentar lo de abajo para ver la pendiente
if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()
