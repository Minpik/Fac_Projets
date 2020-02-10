# encoding=utf8
import pandas as pd
from collections import defaultdict
import numpy as np
from sklearn.tree import DecisionTreeClassifier
clf = DecisionTreeClassifier(random_state=14)
from sklearn.model_selection import cross_val_score
import random
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
import sklearn.metrics

data = pd.read_csv('newData3.csv')
#data = data.dropna()
#data = data[['surface', 'tourney_level', 'winner_name', 'winner_hand', 'winner_rank', 'loser_name', 'loser_hand', 'loser_rank', 'score', 'best_of', 'round', 'w_ace', 'w_df', 'l_ace', 'l_df', 'Player1', 'Player2']]
data = data[['player1_name', 'player2_name', 'player1_rank', 'player2_rank', 'Winner','player1_age','player2_age','player1_rank_points','player2_rank_points'
, 'p1_ace', 'p2_ace', 'surface', 'player1_hand', 'player2_hand', 'best_of','player1_id','player2_id','player1_ht','player2_ht','player1_age','player2_age','p1_df','p2_df']]
#data["Winner Best Rank"] = data["winner_rank"] < data["loser_rank"]
#data = data[['player1_name','player2_name','Winner']]




data["Player1 Win"] = data['Winner'] == data['player1_name']
data = data.dropna()
y_true = data["Player1 Win"].values
print(data.head(5))

data["player1_wins"] = 0
data["player2_wins"] = 0
data["player1_wins_surface"] = 0
data["player2_wins_surface"] = 0
data["player1_lose_surface"] = 0
data["player2_lose_surface"] = 0
data["player1_winStreak"] = 0
data["player2_winStreak"] = 0
data["player1_nbace"] = 0
data["player2_nbace"] = 0
data["player1_nbdf"] = 0
data["player2_nbdf"] = 0
data["player1_lose"] = 0
data["player2_lose"] = 0
def confrontation():
	wins = defaultdict(int)
	lose = defaultdict(int)
	winStreak = defaultdict(int)
	nbAce = defaultdict(int)
	nbDf = defaultdict(int)
	wins_hard = defaultdict(int)
	wins_clay = defaultdict(int)
	wins_grass = defaultdict(int)
	wins_other = defaultdict(int)
	lose_hard = defaultdict(int)
	lose_clay = defaultdict(int)
	lose_grass = defaultdict(int)
	lose_other = defaultdict(int)
	for index, row in data.iterrows():
		row["surface"] = getSurface(row["surface"])
		row["player1_hand"] = getHand(row["player1_hand"])
		row["player2_hand"] = getHand(row["player2_hand"])
		row['player1_wins'] = wins[row['player1_name']]
		row['player2_wins'] = wins[row['player2_name']]
		row['player1_winStreak'] = winStreak[row['player1_name']]
		row['player2_winStreak'] = winStreak[row['player2_name']]
		row['player1_nbace'] = nbAce[row['player1_name']]
		row['player2_nbace'] = nbAce[row['player2_name']]
		row['player1_nbdf'] = nbDf[row['player1_name']]
		row['player2_nbdf'] = nbDf[row['player2_name']]
		row['player1_lose'] = lose[row['player1_name']]
		row['player2_lose'] = lose[row['player2_name']]
		if (row["surface"] == 0):
			row['player1_wins_surface'] = wins_hard[row['player1_name']]
			row['player2_wins_surface'] = wins_hard[row['player2_name']]
			row['player1_lose_surface'] = wins_hard[row['player1_name']]
			row['player2_lose_surface'] = wins_hard[row['player2_name']]
		elif (row["surface"] == 1):
			row['player1_wins_surface'] = wins_clay[row['player1_name']]	
			row['player2_wins_surface'] = wins_clay[row['player2_name']]
			row['player1_lose_surface'] = wins_hard[row['player1_name']]
			row['player2_lose_surface'] = wins_hard[row['player2_name']]
		elif (row["surface"] == 2):
			row['player1_wins_surface'] = wins_grass[row['player1_name']]
			row['player2_wins_surface'] = wins_grass[row['player2_name']]
			row['player1_lose_surface'] = wins_hard[row['player1_name']]
			row['player2_lose_surface'] = wins_hard[row['player2_name']]
		else:
			row['player1_wins_surface'] = wins_other[row['player1_name']]
			row['player2_wins_surface'] = wins_other[row['player2_name']]
			row['player1_lose_surface'] = wins_hard[row['player1_name']]
			row['player2_lose_surface'] = wins_hard[row['player2_name']]
		data.loc[index] = row 
		if (row['player1_name'] == row['Winner']):
			wins[row['player1_name']] += 1
			lose[row['player2_name']] += 1
			winStreak[row['player1_name']] += 1
			winStreak[row['player2_name']] = 0
			if (row["surface"] == 0):
				wins_hard[row['player1_name']] += 1
				lose_hard[row['player2_name']] += 1
			elif (row["surface"] == 1):
				wins_clay[row['player1_name']] += 1
				lose_clay[row['player2_name']] += 1
			elif (row["surface"] == 2):
				wins_grass[row['player1_name']] += 1
				lose_grass[row['player2_name']] += 1
			else:
				wins_other[row['player1_name']] += 1
				lose_other[row['player2_name']] += 1
		else:
			wins[row['player2_name']] += 1
			lose[row['player1_name']] += 1
			winStreak[row['player2_name']] += 1
			winStreak[row['player1_name']] = 0
			if (row["surface"] == 0):
				wins_hard[row['player2_name']] += 1
				lose_hard[row['player1_name']] += 1
			elif (row["surface"] == 1):
				wins_clay[row['player2_name']] += 1
				lose_clay[row['player1_name']] += 1
			elif (row["surface"] == 2):
				wins_grass[row['player2_name']] += 1
				lose_grass[row['player1_name']] += 1
			else:
				wins_other[row['player2_name']] += 1
				lose_other[row['player1_name']] += 1
		nbAce[row['player1_name']] += row["p1_ace"]
		nbAce[row['player2_name']] += row["p2_ace"]
		nbDf[row['player1_name']] += row["p1_df"]
		nbDf[row['player2_name']] += row["p2_df"]


def getSurface(surface):
	if (surface == "Hard"):
		return 0
	elif (surface == "Clay"):
		return 1
	elif (surface == "Grass"):
		return 2
	else:
		return 3

def getHand(hand):
	if (hand == "R"):
		return 0
	elif (hand == "L"):
		return 1
	else:
		return 2
"""data["player1_winStreak"] = 0
data["player2_winStreak"] = 0
def winStreak():
	winStreak = defaultdict(int)
	for index, row in data.iterrows():
		row['player1_winStreak'] = winStreak[row['player1_name']]
		row['player2_winStreak'] = winStreak[row['player2_name']]
		data.loc[index] = row 
		if (row['player1_name'] == row['Winner']):
			winStreak[row['player1_name']] += 1
			winStreak[row['player2_name']] = 0
		else:
			winStreak[row['player2_name']] += 1
			winStreak[row['player1_name']] = 0

"""
confrontation()
#winStreak()
print(data.head(50))

"""scores = cross_val_score(clf, data[['player1_rank', 'player2_rank']], y_true, scoring='accuracy')
print(scores)
print("Using just the last result from the home and visitor teams")
print("Accuracy: {0:.1f}%".format(np.mean(scores) * 100))"""

X_small = data[['player1_rank', 'player2_rank', 'player1_wins','player2_wins','player1_winStreak','player2_winStreak','player1_rank_points','player2_rank_points',
'player1_nbace','player2_nbace','player1_lose','player2_lose', 'player1_wins_surface', 'player2_wins_surface', 'surface', 'player1_hand', 'player2_hand', 
'best_of', 'player1_lose_surface', 'player2_lose_surface','player1_id','player2_id','player1_ht','player2_ht','player1_age','player2_age','player1_nbace','player2_nbace']]
#X_small = data[['player1_rank','player2_rank','player1_rank_points','player2_rank_points','player1_wins_surface','player2_wins_surface']]
X_train, X_test, Y_train, Y_test = train_test_split(X_small, y_true, test_size=.4)
print("YEqsqcs",X_train)
print("YEqs",X_test)
print("Yqcs",Y_train)
print("DAZsqcs",Y_test)

#Build model on training data
"""classifier=DecisionTreeClassifier()
classifier=classifier.fit(X_train,Y_train)

predictions=classifier.predict(X_test)

#sklearn.metrics.confusion_matrix(Y_test,predictions)

sklearn.metrics.accuracy_score(Y_test, predictions)
print("Accuracy: {0:.1f}%".format(sklearn.metrics.accuracy_score(Y_test, predictions) * 100))	

print(classifier.feature_importances_)
"""

from sklearn.ensemble import RandomForestClassifier
model = RandomForestClassifier(n_estimators = 1000,
criterion = "gini", max_features = "sqrt")
model.fit(X_train,Y_train)
predictions = model.predict(X_test)
print(model.feature_importances_)
print(predictions)
print(sklearn.metrics.confusion_matrix(Y_test,predictions))
print("Accuracy: {0:.1f}%".format(sklearn.metrics.accuracy_score(Y_test, predictions) * 100))