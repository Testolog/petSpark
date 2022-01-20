def insertion_sort(lst, invariant=True):
    for j in range(1, len(lst)):
        key = lst[j]  # 2
        i = j - 1  # 5
        while i >= 0 and ((lst[i] > key and invariant) or (lst[i] < key and not invariant)):  # true true
            lst[i + 1] = lst[i]  # 5 -> 2
            i = i - 1  # 0
        lst[i + 1] = key  # 2


def selection_sort(lst):
    for i in range(0, len(lst)):
        min_i = i
        for j in range(i + 1, len(lst)):
            if lst[j] < lst[min_i]:
                min_i = j
        lst[i], lst[min_i] = lst[min_i], lst[i]


l = [5, 2, 4, 6, 1, 3]
# l = [5, 2, 2, 0, 1, 3]
# insertion_sort(l, False)
selection_sort(l)
print(l)
