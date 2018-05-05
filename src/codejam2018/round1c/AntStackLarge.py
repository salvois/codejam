def solve(weights):
    MAX_ANTS = 139
    MAX_WEIGHT = 10000000000
    # Initialize the dp array
    dp = [[MAX_WEIGHT if w > 0 else 0 for w in range(0, MAX_ANTS + 1)] for i in range(0, len(weights) + 1)]
    # Knapsack-like logic
    for i in range(0, len(weights)):
        for w in range(1, MAX_ANTS + 1):
            if dp[i][w - 1] <= 6 * weights[i]:
                dp[i + 1][w] = min(
                        weights[i] + dp[i][w - 1], # use i-th item
                        dp[i][w]) # do not use i-th item
            else:
                # i-th item would not fit into the knapsack
                dp[i + 1][w] = dp[i][w]
    # Find the maximum populated stack size
    result = 0
    for i in range(0, len(weights) + 1):
        for w in range(1, MAX_ANTS + 1):
            if dp[i][w] < MAX_WEIGHT and w > result:
                result = w
    return result

test_count = int(input())
for test_number in range(1, test_count + 1):
    ant_count = input()
    weights = [int(i) for i in input().split(" ")]
    max_stack_size = solve(weights)
    print("Case #{}: {}".format(test_number, max_stack_size))
