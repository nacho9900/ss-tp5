import matplotlib.pyplot as plt
from itertools import zip_longest
from statistics import stdev

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100.png'
systems = []
for i in range(100):
    with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        lines = f.readlines()
        systems.append({
            'time': [float(line.split(' ')[0]) for line in lines],
            'amounts': [200.0-float(line.split(' ')[1]) for line in lines]
        })

def avg_not_none(l):
    l = [i for i in l if i]  # remove Nones
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

def stdev_not_none(l):
    l = [i for i in l if i]  # remove Nones
    if len(l) < 2:
        return 0
    return stdev(l)

list_of_amounts = [system['amounts'] for system in systems]
avg_amounts = [avg_not_none(elem) for elem in zip_longest(*list_of_amounts)]
stdev_amounts = [stdev_not_none(elem) for elem in zip_longest(*list_of_amounts)]
stdev_amounts_limited = [(i % 100 == 0)*stdev for i, stdev in enumerate(stdev_amounts)]

fig = plt.figure(figsize=(15,10))
ax1 = fig.add_subplot(111)
ax1.set_xlabel('Tiempo (s)', fontsize=27)
ax1.set_ylabel('Particulas restantes', fontsize=27)
ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
ax1.errorbar(systems[0]['time'], avg_amounts, yerr=stdev_amounts)
# ax1.set_aspect( 1 )
# plt.xlim([0, 6])
# plt.ylim([0, 6])
fig1=plt.gcf()

if savefile_name != '':
    plt.savefig(savefile_name)
else:
    plt.show()
