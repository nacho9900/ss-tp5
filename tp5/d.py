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
        with open(f'ej_c/flowPerOpening_N_{N}_opening_{d_string(d)}_dt_1e-4_seed_{i}.csv') as f:
        # with open(f'ej_c_IO/flowPerOpening_N_{N}_opening_{d_string(d)}_dt_1e-4_seed_{i}.csv') as f:
            lines = f.readlines()
            list_of_times.append([float(line.split(' ')[0]) for line in lines])
    
    # Set up data
    y = [i for i in range(N)]
    i1 = int(len(y) * 0.4)
    i2 = int(len(y) * 0.6)
    x = [(y[i2] - y[i1]) / (times[i2] - times[i1]) for times in list_of_times]

    return avg(x), stdev(x)

def calc_beverloo(N, d):
    density = 1.15
    force = 320
    beverloo = density * math.sqrt(force / 80) * (d - 0.3)**1.5
    return beverloo

caudales = []
caudaleserr = []
beverloos = []
confs = [(200, 1.2), (260, 1.8), (320, 2.4), (380, 3.0)]
# confs = [(200, 1.2), (200, 1.8), (200, 2.4), (200, 3.0)]
for N, d in confs:
    caudal, caudalerr = calc_caudal(N, d)
    caudales.append(caudal)
    caudaleserr.append(caudalerr)
    beverloos.append(calc_beverloo(N, d))

# Init graph
fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Apertura de salida (m)', fontsize=27)
ax1.set_ylabel('Caudal (particulas/s)', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)

# Plot
ds = [conf[1] for conf in confs]
ax1.errorbar(ds, caudales, yerr=caudaleserr, fmt='o', label='Caudal medio')
ax1.errorbar(ds, beverloos, fmt='-', label='Ajuste beverloo')
fig1=plt.gcf()
ax1.legend(fontsize=27, loc='upper left')
ax1.grid()

# Descomentar lo de abajo para ver la pendiente
if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()
