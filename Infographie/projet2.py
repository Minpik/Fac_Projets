# encoding=utf8
import pandas as pd
import random

data = pd.read_csv('atp_matches_2016.csv')
#data = data.dropna()
data["Winner"] = data['player1_name']
#print(data)
#data.to_csv(r'newData.csv')

def new_data():
	for index, row in data.iterrows():
		row["Winner"] = row["player1_name"]
		tmp = row[['player1_id', 'player1_seed','player1_entry', 'player1_name', 'player1_hand', 'player1_ht', 'player1_ioc','player1_age', 'player1_rank', 'player1_rank_points']]
		r = random.randint(0, 1)
		if (r == 0):
			tmp2 = row[['player2_id', 'player2_seed','player2_entry', 'player2_name', 'player2_hand', 'player2_ht', 'player2_ioc','player2_age', 'player2_rank', 'player2_rank_points']]
			row[['player2_id', 'player2_seed','player2_entry', 'player2_name', 'player2_hand', 'player2_ht', 'player2_ioc','player2_age', 'player2_rank', 'player2_rank_points']] = tmp
			row[['player1_id', 'player1_seed','player1_entry', 'player1_name', 'player1_hand', 'player1_ht', 'player1_ioc','player1_age', 'player1_rank', 'player1_rank_points']] = tmp2
			data.loc[index] = row

	data.to_csv(r'newData3.csv')

new_data()
"""
def split_lines(input, seed, output1, output2):
    fichier = open(output1, "a")
    fichier2 = open(output2, "a")
    random.seed(seed)

    for line in open(input, 'r').readlines():
        a = random.randint(0, 1)
        if (a == 0):
            fichier.write(line)
        else:
            fichier2.write(line)

def read_data(filename):
	X = []
	Y = []
	for line in open(filename, 'r').readlines():
		data = line.split(',')
		if(data[3] == data[16]):
			Y.append(True)
		else:
			Y.append(False)
		tab = []
		for i in data:
			tab.append(i)
		X.append(tab)
	return X,Y


def new_data():
	data["Player1"] = ""
	data["Player2"] = ""
	for index, row in data.iterrows():
		r = random.randint(0, 1)
		if (r == 0):
			row["Player1"] = row["winner_name"]
			row["Player2"] = row["loser_name"]
		else:
			row["Player1"] = row["loser_name"]
			row["Player2"] = row["winner_name"]

		data.loc[index] = row

	data.to_csv(r'newData.csv')

#new_data()
#print(data.head(5))

rank = defaultdict(int)
def ratio(player1_name, player2_name, surface):
	for index, row in data.iterrows():
		if (row["winner_name"] == player1_name):
			rank["Rank " + player1_name]  = row["winner_rank"]
		elif (row["loser_name"] == player1_name):
			rank["Rank " + player1_name] = row["loser_rank"]
		if (row["winner_name"] == player2_name):
			rank["Rank " + player2_name]  = row["winner_rank"]
		elif (row["loser_name"] == player2_name):
			rank["Rank " + player2_name]  = row["loser_rank"]

		if (len(rank) == 2):
			break

	
		if (row["surface"] == surface):
			winner = row["winner_name"]
			loser = row["loser_name"]
			if (player1_name == player1 and player2_name == player2):
				win[player1 + "VS" + player2] += 1
				row["Win VS " + player2] = win[player1 + "VS" + player2]
			if (player1_name == winner or player2_name == winner):
				# Set current win
				win[winner] += 1
	
	for index, row in data.iterrows():
		if (row["surface"] == surface):
			winner = row["winner_name"]
			loser = row["loser_name"]
			if (winner == player1_name):
				row["Player1 Win"] = win[winner]
				row["Player2 Win"] = win[player2_name]
				data.loc[index] = row
			elif (winner == player2_name):
				row["Player1 Win"] = win[winner]
				row["Player2 Win"] = win[player1_name]
				data.loc[index] = row
	
	print(data.head(5))
	print(rank)

"""

#split_lines('newData.csv', 7, 'train', 'test')
"""train_x, train_y = read_data('train')
test_x, test_y = read_data('test')
print(train_x, train_y)"""

"""for x in data[['winner_rank', 'loser_rank']].values:
	print(x)
"""
#x_train, y_train = read_data('train')