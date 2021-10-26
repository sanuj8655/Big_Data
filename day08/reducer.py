#!/usr/bin/python3
import sys

di = dict()
for line in sys.stdin:
	(word,cnt) = line.split()
	newcnt = di.get(word, 0) + int(cnt)
	di[word] = newcnt
for word,total in di.items():
	print(f"{word}\t{total}")
