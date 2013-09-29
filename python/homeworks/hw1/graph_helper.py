# from rpy import *
import matplotlib.pyplot as plt

def plot_line(original_plot, threshold, bounded_plot):
	fig = plt.figure()

	print threshold

	if threshold:
		plt.ylim(original_plot['original']['yLim'])
		plt.subplot(111)
		plt.plot(original_plot['original']['yValues'])
		plt.ylabel('% Documents')
		plt.title('Term vs Rarity')

		plt.subplot(211)		
		# plt.ylim(original_plot['original']['yLim'])
		plt.plot(bounded_plot['bounded']['xValues'], bounded_plot['bounded']['yValues'])
		plt.xlabel('Terms')
		plt.ylabel('% Documents')
	else:
		plt.ylim(original_plot['original']['yLim'])
		# plt.subplot(111)
		plt.plot(original_plot['original']['yValues'])
		plt.xlabel('Terms')
		plt.ylabel('% Documents')
		plt.title('Term vs Rarity')
		
	# if xValues == None:
	# 	plt.plot(yValues)
	# else:
	# 	plt.plot(xValues, yValues)
	# fig.savefig('output.png')

	# fig = plt.figure()

	# plt.ylim(original_plot['original']['yLim'])
	
	# frame = plt.gca()

	# # frame.axes.get_xaxis().set_visible(False)
	# # frame.axes.get_yaxis().set_visible(False)

	# plt.title('Term vs Rarity')

	# plt.subplot(211)
	# plt.plot(original_plot['original']['yValues'], 'ro')
	# plt.text(1000, 0.8, r'Terms sorted by TermID')

	# plt.ylabel('% Documents')
	# plt.title('Term vs Rarity')

	# plt.subplot(212)
	# plt.plot(sorted(original_plot['original']['yValues'], reverse=True), 'ro')
	# plt.xlabel('Terms')
	# plt.ylabel('% Documents')
	# plt.text(1000, 0.8, r'Terms sorted by Rarity')
	fig.savefig('output.png')