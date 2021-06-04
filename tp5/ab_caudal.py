import matplotlib.pyplot as plt
from statistics import stdev

savefile_name = ''  # if name is empty, script will show and not save the graph
# savefile_name = 'ej_a/particlesOverTime_100.png'
list_of_times = []
for i in range(100):
    with open(f'ej_a/particlesOverTime_N_200_opening_1_2_dt_1e-2_seed_{i}.csv') as f:
        lines = f.readlines()
        list_of_times.append([float(line.split(' ')[0]) for line in lines])

def avg(l):
    if len(l) == 0:
        return 0
    return sum(l)/len(l)

# Set up data - find average amounts for each timestep (very inefficient, I don't care)
granularity = 50  # 50 points per second
times = [i/granularity for i in range(1000 * granularity)]
amounts_avg = []
for time in times:
    amounts_for_time = []
    for _times in list_of_times:
        for amount, _time in enumerate(_times):
            if _time > time:
                amounts_for_time.append(amount)  # o amount-1
                break
    if len(amounts_for_time) != 0:
        amounts_avg.append(avg(amounts_for_time))
    else:
        times = times[:len(amounts_avg)]
        break

x2 = int(0.7 * len(amounts_avg))
x1 = int(0.2 * len(amounts_avg))
print('Caudal (pendiente estable):', (amounts_avg[x2] - amounts_avg[x1]) / (x2 - x1) * granularity)

# Descomentar todo lo de abajo para ver la pendiente
# fig = plt.figure(figsize=(15,10))
# ax1 = fig.add_subplot(111)
# ax1.set_xlabel('Tiempo (s)', fontsize=27)
# ax1.set_ylabel('Particulas que escaparon', fontsize=27)
# ax1.tick_params(axis='both', which='major', labelsize=20, width=2.5, length=10)
# ax1.errorbar(times, amounts_avg)
# # ax1.set_aspect( 1 )
# # plt.xlim([0, 6])
# # plt.ylim([0, 6])
# fig1=plt.gcf()

# if savefile_name != '':
#     plt.savefig(savefile_name)
# else:
#     plt.show()
