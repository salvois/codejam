"""
Ant Stack problem from Round 1C of Google Code Jam 2018.
https://codejam.withgoogle.com/2018/challenges/0000000000007765/dashboard/000000000003e0a8

Script to generate the largest ant stack starting from lightest ants.
Turns out the largest stack is made of 139 such ants.

Released into the public domain by Salvatore Isaja.
"""

import math

stack_weight = 0
last_weight = 1
while last_weight <= 1_000_000_000:
	if last_weight * 6 >= stack_weight:
		print("{}\t{}".format(stack_weight, last_weight))
		stack_weight += last_weight
	else:
		last_weight = math.ceil(stack_weight / 6)
