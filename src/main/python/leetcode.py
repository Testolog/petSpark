from typing import Optional, List


class ListNode:
    def __init__(self, val, next=None):
        if type(val) == list:
            if len(val) == 1:
                self.next = None
                self.val = val[0]
            else:
                self.val = val[0]
                self.next = ListNode(val[1:])
        else:
            self.val = val
            self.next = next

    def __add__(self, other):
        if self.next == None:
            self.next = ListNode(other, None)
        else:
            self.next += other
        return self

    def __str__(self):
        return f"val:{self.val}->{str(self.next)}"


class Solution:
    def longestPalindrome(self, s: str) -> str:
        len_input = len(s)
        if len_input <= 1:
            return s
        if len_input == 2:
            return s if s[0] == s[1] else s[0]
        res = s[0]
        for i in range(0, len_input - 1):
            follow = s[i]
            for j in s[i + 1:]:
                follow += j
                if follow == follow[::-1] and len(res) < len(follow):
                    res = follow
        # print(iters)
        return res

    def convert(self, s: str, numRows: int) -> str:
        # https://leetcode.com/problems/zigzag-conversion/
        full_col = int(len("PAYPALISHIRING") / numRows)
        res = [""] * (numRows + 1)
        print(res)
        for i in range(numRows + 1):
            res[i] = [s[i:numRows]]
        print(res)
        return ""

    def reverse(self, x: int) -> int:
        reverse = 0
        neg = 1 if x > 0 else -1
        x = x * neg
        while x != 0:
            remainder = x % 10
            reverse = reverse * 10 + remainder
            if bool(reverse >> 31):
                reverse = 0
                x = 0
            x = int(x / 10)
        return reverse * neg

    def isPalindrome(self, x: int) -> bool:
        if x < 0:
            return False
        i = x
        r = 0
        while i != 0:
            l = i % 10
            r = r * 10 + l
            i = i / 10
        return r == x

    def isValid(self, s: str) -> bool:
        q = []
        m = {
            "(": ")",
            "{": "}",
            "[": "]"
        }
        for _s in s:
            if _s not in "({[":
                if m[q.pop(-1)] != _s:
                    return False
            else:
                q.append(_s)
        return len(q) == 0

    def mergeTwoLists(self,
                      list1: Optional[ListNode],
                      list2: Optional[ListNode]) -> Optional[ListNode]:
        if not list1 or not list2:
            return list1 or list2
        if list1.val > list2.val:
            smallest_head = list2
            next_value = list2.next
            biggest_head = list1
        else:
            smallest_head = list1
            next_value = list1.next
            biggest_head = list2
        while next_value and biggest_head:
            if next_value.val > biggest_head.val:
                smallest_head.next = biggest_head
                biggest_head = biggest_head.next
            else:
                smallest_head.next = next_value
                next_value = next_value.next
            smallest_head = smallest_head.next
        if biggest_head:
            smallest_head.next = biggest_head
        if next_value:
            smallest_head.next = next_value
        return list2 if list1.val > list2.val else list1

    def generateParenthesis(self, n: int) -> List[str]:
        pass

    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        def min_val_index(l: List[Optional[ListNode]]) -> int:
            if len(l) == 1:
                return 0
            index_min = 0
            for i in range(len(l)):
                if l[index_min].val > l[i].val:
                    index_min = i
            return index_min

        not_null_lists = [simple_list for simple_list in lists if simple_list]
        if len(not_null_lists) == 0:
            return None
        if len(not_null_lists) == 1:
            return not_null_lists[0]
        found_index = min_val_index(not_null_lists)
        head = moved_head = not_null_lists.pop(found_index)
        head_next = head.next
        while len(not_null_lists) > 0:
            if not head_next:
                head_next = not_null_lists.pop(0)
            else:
                index = min_val_index(not_null_lists)
                if not_null_lists[index].val > head_next.val:
                    moved_head.next = head_next
                    head_next = head_next.next
                    moved_head = moved_head.next
                else:
                    moved_head.next = not_null_lists[index]
                    moved_head = moved_head.next
                    if not_null_lists[index].next:
                        not_null_lists[index] = not_null_lists[index].next
                    else:
                        del not_null_lists[index]
        if head_next:
            moved_head.next = head_next
        return head

    def swapPairs(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if not head:
            return None
        if not head.next:
            return head
        first_node = head.next
        left_head = first_node.next
        first_node.next = head
        second_node = first_node.next
        second_node.next = left_head
        while left_head and left_head.next:
            f_node = left_head
            s_node = f_node.next
            left_head = s_node.next
            f_node.next = left_head
            s_node.next = f_node
            second_node.next = s_node
            second_node = second_node.next.next
        if left_head:
            second_node.next = left_head
        return first_node

    def strStr(self, haystack: str, needle: str) -> int:
        res = -1
        for i in range(len(haystack)):
            t = haystack[i]
            if t == needle:
                return i
            j = i + 1
            while t == needle[:len(t)] and j < len(haystack):
                t += haystack[j]
                if t == needle:
                    return i
                j += 1
        return res

    def divide(self, dividend: int, divisor: int) -> int:
        res = 0
        is_neg = not ((divisor < 0 and dividend < 0) or (divisor >= 0 and dividend >= 0))
        dividend = abs(dividend)
        divisor = abs(divisor)
        for i in range(31, -1, -1):
            if divisor << i <= dividend:
                dividend -= divisor << i
                res += 1 << i
        return -res if is_neg else res - 1 if 1 << 31 == res else res

    def findSubstring(self, s: str, words: List[str]) -> List[int]:
        def count_mask(lst: List[str]):
            dct = {}
            for tmp_string in lst:
                if tmp_string in dct:
                    dct[tmp_string] += 1
                else:
                    dct[tmp_string] = 1
            return dct

        mask_words = count_mask(words)
        chunk_size = len(words[0])
        chunk_numbers = len(words)
        result = []
        for step in range(len(s)):
            groups_common_len = step + chunk_numbers * chunk_size
            if groups_common_len > len(s):
                return result
            sub_string = s[step:groups_common_len]
            mask_sub_string = {}
            for i in range(0, len(sub_string), chunk_size):
                p = sub_string[i:i + chunk_size]
                if p in mask_sub_string:
                    mask_sub_string[p] += 1
                else:
                    mask_sub_string[p] = 1
            if mask_sub_string == mask_words:
                result.append(step)
        return result

    # def longestValidParentheses(self, s: str) -> int:
    #     lng = 0
    #     for i in range(len(s), -1, -1):
    #         index = 0
    #         open_s = 0
    #         k = i
    #         l = len(s) - 1
    #         while k < l:
    #             if s[l] == "(":
    #                 l -= 1
    #             elif s[k] == "(":
    #                 open_s += 1
    #                 k += 1
    #             else:
    #                 open_s -= 1
    #                 index += 1
    #                 k += 1
    #                 if open_s < 0:
    #                     index = 0
    #                     open_s = 0
    #                 elif open_s == 0:
    #                     if index > lng:
    #                         lng = index
    #
    #     return lng * 2

    def longestValidParentheses(self, s: str) -> int:
        """
        Instead of finding every possible string and checking its validity, we can make use of a stack while scanning the given string to:
        Check if the string scanned so far is valid.
        Find the length of the longest valid string.
        In order to do so, we start by pushing -1−1 onto the stack. For every \text{‘(’}‘(’ encountered, we push its index onto the stack.

        For every \text{‘)’}‘)’ encountered, we pop the topmost element. Then, the length of the currently encountered valid string of parentheses will be the difference between the current element's index and the top element of the stack.

        If, while popping the element, the stack becomes empty, we will push the current element's index onto the stack. In this way, we can continue to calculate the length of the valid substrings and return the length of the longest valid string at the end.

        See this example for a better understanding.
        :param s:
        :return:
        """
        lng = 0
        stack = []
        stack.append(-1)
        for i in range(len(s)):
            if s[i] == "(":
                stack.append(i)
            else:
                stack.pop()
                if len(stack) == 0:
                    stack.append(i)
                else:
                    if i - stack[-1] > lng:
                        lng = i - stack[-1]
        return lng

    def searchRange(self, nums: List[int], target: int) -> List[int]:
        def binary_search(low, high):
            if low > high:
                return None
            mid = int((low + high) / 2)
            if target == nums[mid]:
                return mid
            elif target > nums[mid]:
                return binary_search(mid + 1, high)
            elif target < nums[mid]:
                return binary_search(low, mid - 1)

        start_index = binary_search(0, len(nums))
        end_index = start_index
        while nums[start_index] == nums[end_index] or end_index >= len(nums):
            end_index += 1
        return [start_index, end_index - 1]

    def searchInsert(self, nums: List[int], target: int) -> int:
        def binary_search(low, high):
            if low > high:
                return high + 1
            mid = int((low + high) / 2)
            target_distance = abs(target - nums[mid])
            if target == nums[mid]:
                return mid
            elif target > nums[mid]:
                return binary_search(mid + 1, high)
            elif target < nums[mid]:
                return binary_search(low, mid - 1)

        return binary_search(0, len(nums) - 1)
        # "()()()"

    def isValidSudoku(self, board: List[List[str]]) -> bool:
        start = 1
        for index in range(0, 9):
            tmp = []
            for x in range(0, 9):
                if board[index][x] != ".":
                    if board[index][x] not in tmp:
                        tmp.append(board[index][x])
                    else:
                        return False
            tmp = []
            for y in range(0, 9):
                if board[y][index] != ".":
                    if board[y][index] not in tmp:
                        tmp.append(board[y][index])
                    else:
                        return False
            y = start + 3 * (index // 3)
            x = start + 3 * (index % 3)
            tmp = []
            for i in range(-1, 2):
                for j in range(-1, 2):
                    if board[y - i][x - j] != ".":
                        if board[y - i][x - j] not in tmp:
                            tmp.append(board[y - i][x - j])
                        else:
                            return False
        return True

    def combinationSum2(self, candidates: List[int], target: int) -> List[List[int]]:
        candidates = sorted(candidates)
        res = []

        def go(index, prev, left):
            for j in range(index, -1, -1):
                if left == candidates[j]:
                    res.append(prev + [candidates[j]])
                else:
                    m = (left - candidates[j]) // candidates[j]
                    l = (left - candidates[j]) % candidates[j]
                    j_m = [candidates[j]] * m
                    if m >= 1 and l == 0:
                        res.append(prev + j_m)
                    else:
                        for c in range(j):
                            if candidates[c] == left:
                                if j_m + [candidates[c]] not in res:
                                    res.append(j_m + [candidates[c]])
                            else:
                                go(c, prev + j_m + [candidates[c]], left - candidates[j])

        for i in range(len(candidates)):
            if candidates[i] > target:
                return res
            if candidates[i] == target:
                res.append([candidates[i]])
                return res
            go(i, [candidates[i]], target)
        return res

    def combinationSum(self, candidates: List[int], target: int) -> List[List[int]]:
        # if sum(candidates) == target:
        #     return [candidates]
        if len(candidates) == 1 and candidates[0] != target:
            return []
        res = []
        for n in range(len(candidates)):
            cur = candidates[n]
            mult = target // cur
            for m in range(mult, 0, -1):
                left = target - (cur * m)
                if left == 0:
                    res.append([cur] * m)
                else:
                    if left == cur != cur:
                        res.append([cur] * m + [cur])
        return res

    def comb(self, l: List[int]):
        def go(last, res, number):
            if number == len(l):
                return res
            p = [t + [k] for k in l for t in last]
            res += p
            return go(p, res, number + 1)

        return go([[_] for _ in l], [[_] for _ in l], 1)

    def combNormal(self, lst: List[int]):
        if len(lst) == 0:
            return [[]]
        first = lst[0]
        rest = lst[1:]
        without = self.combNormal(rest)
        with_f = [w + [first] for w in without]
        return without + with_f

    def firstMissingPositive(self, nums: List[int]) -> int:
        all_pos = [_ for _ in range(0, len(nums) + 1)]
        len_dt = len(nums)
        for i, n in enumerate(nums):
            if n <= 0:
                continue
            if n <= len_dt:
                all_pos[n] = 0

        for n in all_pos:
            if n > 0:
                return n
        else:
            return len_dt + 1

    # garbage
    def trap(self, height: List[int]) -> int:
        left = 0
        right = len(height) - 1
        left_max = 0
        right_max = 0
        res = 0
        while left < right:
            if height[left] < height[right]:
                if height[left] >= left_max:
                    left_max = height[left]
                else:
                    res += left_max - height[left]
                left += 1
            else:
                if height[right] >= right_max:
                    right_max = height[right]
                else:
                    res += right_max - height[right]
                right -= 1
        return res

    def hasCycle(self, head: Optional[ListNode]) -> bool:
        tmp = head.next
        while tmp:
            h = head
            n = head.next
            while tmp != n:
                h = n
                n = h.next

            tmp = tmp.next
        return False

    def jump(self, nums: List[int]) -> int:
        min_jump = nums[0]
        for i, n in enumerate(nums):
            if n <= nums[i + 1] and n < min_jump:
                min_jump = n
        return min_jump

    def addTwoNumbers(self, l1: Optional[ListNode], l2: Optional[ListNode], left=0) -> Optional[ListNode]:
        head = l1 or l2
        while l1.next and l2.next:
            l1.val = l2.val = l2.val + l1.val + left
            if l1.val >= 10:
                left = 1
                l1.val = l2.val = l1.val % 10
            else:
                left = 0
            l1 = l1.next
            l2 = l2.next
        if not l1.next and not l2.next:
            l1.val = l2.val = l2.val + l1.val + left
            if l1.val >= 10:
                l1.val = l2.val = l1.val % 10
                l1.next = l2.next = ListNode(1)
        else:
            l1.val = l2.val = l2.val + l1.val + left
            if l1.val >= 10:
                l1.val = l2.val = l2.val % 10
                left = 1
            else:
                left = 0
            l1.next = l2.next = l1.next or l2.next
            l1 = l1.next
            while left > 0 and l1.next:
                l1.val += left
                if l1.val >= 10:
                    l1.val %= 10
                    left = 1
                else:
                    left = 0
                l1 = l1.next
            if not l1.next and left > 0:
                l1.val += left
                if l1.val == 10:
                    l1.val %= 10
                    l1.next = ListNode(1)
        return head

    def isMatch(self, s: str, p: str) -> bool:
        isIn = [None] * len(s)

        def _go(s_i: int, p_i):
            if s_i == len(s): return
            if p_i == len(p): return
            if p[p_i] == "*":
                p_i += 1
                while s_i < len(s):
                    _go(s_i, p_i)
                    s_i += 1
                    isIn[s_i] = True
            elif p[p_i] == "?":
                isIn[s_i] = True
                _go(s_i + 1, p_i + 1)
            else:
                isIn[s_i] = s[s_i] == p[p_i]
                _go(s_i + 1, p_i + 1)

        r = _go(0, 0)
        return r

    def permute(self, nums: List[int]) -> List[List[int]]:
        if len(nums) <= 1:
            yield nums
            return
        for perm in self.permute(nums[1:]):
            for i in range(len(nums)):
                yield perm[:i] + nums[0:1] + perm[i:]

    # [ j, j, j
    # i[1, 2, 3],
    # i[4, 5, 6],
    # i[7, 8, 9]
    # ]
    def rotate(self, matrix: List[List[int]]) -> None:
        shift = len(matrix) - 1
        layers = (shift + 1) // 2
        for layer in range(layers):
            for c in range(layer, shift - layer):
                f = matrix[layer][c]
                s = matrix[c][shift - layer]
                t = matrix[shift - layer][shift - c]
                ff = matrix[shift - c][layer]
                matrix[layer][c] = ff
                matrix[c][shift - layer] = f
                matrix[shift - layer][shift - c] = s
                matrix[shift - c][layer] = t

    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        shift = len(matrix) - 1
        layers = (shift + 1) // 2
        out = []
        for layer in range(layers):
            for d in range(1, 5):
                f = matrix[layer][:]
                t = matrix[shift - layer][:]

        return out

    def canJump(self, nums: List[int]) -> bool:
        if len(nums) <= 1:
            return True
        head = nums[0]
        # if len(nums) == 0:
        #     return False
        if len(nums) - head <= 1:
            return True
        for i in range(head + 1, 1, -1):
            n = nums[i]
            if n != 0:
                if len(nums) - (n + i) <= 1:
                    return True
                print(i, n, head, f"[{nums[i + n:]}]", f"start:{i + n}", f"value:{nums[i + n]}")
                if self.canJump(nums[i:]):
                    return True
        return len(nums) == 1
        # return self.canJump(nums[head:])

    def multiply(self, num1: str, num2: str) -> str:
        if num1 == 0 or num2 == 0:
            return "0"
        f = num1 if len(num1) < len(num2) else num2
        s = num1 if len(num1) >= len(num2) else num2
        result = []
        for index, ff in enumerate(f[::-1], start=0):
            left = "0"
            t = ""
            for ss in s[::-1]:
                n = (int(ff) * int(ss)) + int(left)
                left = str(n // 10)
                t += str(n % 10)
            t = t + left if left != "0" else t
            t = t[::-1]
            t += "0" * index
            result.append(t)
        result = sorted(result, key=lambda x: len(x))
        start = list(result[-1])
        for d in result[:-1]:
            is_left = "0"
            for index in range(-1, (-1 * len(d)) - 1, -1):
                n = int(start[index]) + int(d[index]) + int(is_left)
                is_left = str(n // 10)
                start[index] = str(n % 10)
            if is_left != "0":
                for i in range((-1 * len(d)) - 1, (-1 * len(start)) - 1, -1):
                    n = int(start[i]) + int(is_left)
                    is_left = str(n // 10)
                    start[i] = str(n % 10)

        return "".join(start)

    def minPathSum(self, grid: List[List[int]]) -> int:
        for m in range(1, len(grid[0])):
            grid[0][m] += grid[0][m - 1]

        for n in range(1, len(grid)):
            grid[n][0] += grid[n - 1][0]

        for n in range(1, len(grid)):
            for m in range(1, len(grid[0])):
                grid[n][m] = min(grid[n - 1][m], grid[n][m - 1]) + grid[n][m]
        return grid[-1][-1]

    # znak,dot,number
    # dot,number
    # number,dot,
    # number,dot,number
    def isNumber(self, s: str) -> bool:
        a_numbs = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"]
        a_znak = ["+", "-"]

        def _checker(f, dot=-1):
            p = ["" for _ in range(len(f))]
            znak = -1
            for i in range(0, len(f)):
                if f[i] in a_znak:
                    if znak == -1:
                        znak = i
                    else:
                        return False
                elif f[i] == ".":
                    if dot == -1:
                        dot = i
                    else:
                        return False
                elif f[i] in a_numbs:
                    p[i] = "n"
                else:
                    return False
            if znak != -1 and znak != 0:
                return False
            if dot == 0 and p[dot + 1] != "n":
                return False
            elif dot > 0:
                return p[dot - 1] in ["z", "n"] and True if dot == len(f) - 1 else p[dot + 1] == "n"
            else:
                return all(_ == "n" for _ in p[1::])

        if len(s) == 1:
            return s[0].isdigit()
        s = s.lower().strip().split("e")
        return _checker(s[0]) and _checker(s[1], -2) if len(s) == 2 else _checker(s[0])

    def reverseKGroup(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        # resolved but todo
        if not head:
            return None
        if not head.next:
            return head
        result_head = None
        next_window = head
        while next_window:
            i = 0
            prev = next_window
            temporary_head = next_window
            while i < k:
                prev = temporary_head
                temporary_head = temporary_head.next
                if not temporary_head:
                    return result_head
                i += 1
            if not result_head:
                result_head = prev
            local_head_res = None
            local_head = next_window
            while local_head.next != prev:
                right_head = local_head.next
                next_value = right_head.next
                local_head.next = next_value
                right_head.next = local_head
                local_head = right_head
                if not local_head_res:
                    local_head_res = local_head
                local_head = local_head.next
            next_window = temporary_head
        return result_head


# 3,1,2
solution = Solution()
print(solution.reverseKGroup(ListNode([1, 2, 3, 4, 5]), 2))
# 1,2,3,4,5,6

# 1,2,3
# 2,1,3
# 2,3,1
# 3,2,1

# 1,2,3,4
# 2,1,3,4
# 2,3,1,4
# 2,3,4,1
# 2,3,4,1


# find k element
# 3
