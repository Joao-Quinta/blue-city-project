from ecdsa import SigningKey, NIST256p
from ecdsa.ellipticcurve import Point


class Node1:
    def __init__(self, name, _private_key_hex=None, left=None, right=None):
        self.name = name
        self._private_key = None
        self._private_key_hex = _private_key_hex
        self.public_key = None
        self.sibling = None
        self.sibling_public_key = None
        self.left = left
        self.right = right
        self.parent = None
        if (left is None) and (right is None):
            self.is_leaf = True
        else:
            self.insert_children(left, right)
        if not (_private_key_hex is None):
            self.create_key_pair(_private_key_hex)

    def insert_children(self, left, right):
        self.left = left
        self.right = right
        self.is_leaf = False
        left.set_parent(self)
        right.set_parent(self)
        left.assign_sibling(right)
        right.assign_sibling(left)

    def create_key_pair(self, private_key_hex):
        self.set_private_key(private_key_hex)
        if self.sibling is not None:
            self.sibling.set_sibling_public_key(self.public_key)

    def assign_sibling(self, sibling):
        self.set_sibling(sibling)

    def compute_parent_key(self):
        private_key = self._private_key
        sibling_public_key = self.sibling_public_key
        if (private_key is not None) and (sibling_public_key is not None):
            print("I am " + self.name + " and i am computing my parents key")
            curve = NIST256p
            x = sibling_public_key.pubkey.point.x()
            y = sibling_public_key.pubkey.point.y()
            point = Point(curve.curve, x, y)
            shared_secret_point = private_key.privkey.secret_multiplier * point
            shared_secret_hex = "{:x}".format(shared_secret_point.x())
            self.parent.set_private_key(shared_secret_hex)

    def set_private_key(self, private_key_hex):
        private_key_bytes = bytes.fromhex(private_key_hex)
        self._private_key = SigningKey.from_string(private_key_bytes, curve=NIST256p)
        self.public_key = self._private_key.get_verifying_key()
        if self.sibling is not None:
            self.sibling.set_sibling_public_key(self.public_key)

    # Getters
    def get_public_key(self):
        return self.public_key

    def get_name(self):
        return self.name

    def get_parent(self):
        return self.parent

    def get_sibling(self):
        return self.sibling

    def get_sibling_public_key(self):
        return self.sibling_public_key

    def get_left_child(self):
        return self.left

    def get_right_child(self):
        return self.right

    def get_is_leaf(self):
        return self.is_leaf

    # Setters
    def set_parent(self, node):
        self.parent = node

    def set_name(self, name):
        self.name = name

    def set_public_key(self, public_key):
        self.public_key = public_key

    def set_sibling(self, sibling):
        self.sibling = sibling
        self.set_sibling_public_key(sibling.get_public_key())

    def set_left_child(self, left):
        self.left = left
        if self.right is not None:
            self.left.assign_sibling(self.right)
            self.right.assign_sibling(self.left)

    def set_right_child(self, right):
        self.right = right
        if self.left is not None:
            self.right.assign_sibling(self.left)
            self.left.assign_sibling(self.right)

    def set_is_leaf(self, is_leaf):
        self.is_leaf = is_leaf

    def set_sibling_public_key(self, public_key):
        self.sibling_public_key = public_key


class Tree:
    def __init__(self):
        self.list_leaves = []

    def add_node(self, leaf):
        self.list_leaves.append(leaf)
        if len(self.list_leaves) % 2 == 0:
            return


def compute_previous_two(cc):
    cc1 = cc[0:2] + str(int(cc[-1]) - 2)
    cc2 = cc[0:2] + str(int(cc[-1]) - 1)
    return cc1, cc2


def compute_parent_cc(cc1):
    cc = str(int(cc1[0]) + 1) + ";" + str(int(int(cc1[-1]) / 2))
    return cc


class BinaryTree:
    def __init__(self):
        self.root = None
        self.nodes = {}
        self.current_cc = "0;0"

    def add_leaf(self, name, _private_key_hex):
        new_leaf = Node(name, self.current_cc, _private_key_hex=_private_key_hex)
        self.nodes[self.current_cc] = new_leaf
        self.current_cc = self.current_cc[0:2] + str(int(self.current_cc[-1]) + 1)
        if int(self.current_cc[-1]) % 2 == 0:
            cc1, cc2 = compute_previous_two(self.current_cc)
            self.nodes[cc1].set_sibling(self.nodes[cc2], self)

    def add_node(self, name, cc, left_cc, right_cc):
        new_node = Node(name, cc, _private_key_hex=None, leaf=False, left=left_cc, right=right_cc)
        self.nodes[cc] = new_node
        if int(new_node.cc[-1]) % 2 == 1:
            sibling_cc = new_node.cc[0:2] + str(int(new_node.cc[-1]) - 1)
            self.nodes[sibling_cc].set_sibling(self.nodes[new_node.cc], self)


class Node:
    def __init__(self, name, cc, _private_key_hex=None, leaf=True, left=None, right=None):
        self.name = name
        self.cc = cc
        self.is_leaf = leaf
        self.left_cc = left
        self.right_cc = right
        self._private_key_hex = _private_key_hex
        self._private_key = None
        self.public_key = None
        self.sibling_cc = None
        self.parent_cc = None
        if leaf:
            self._private_path_to_root = []
            self.public_path_to_root = []
            self.co_path_to_root = []
            self.set_private_key(_private_key_hex)
        else:
            self._private_path_to_root = None
            self.public_path_to_root = None
            self.co_path_to_root = None

    def set_private_key(self, private_key_hex):
        private_key_bytes = bytes.fromhex(private_key_hex)
        self._private_key = SigningKey.from_string(private_key_bytes, curve=NIST256p)
        self._private_path_to_root.append(self._private_key)
        self.public_key = self._private_key.get_verifying_key()
        self.public_path_to_root.append(self.public_key)

    def set_sibling(self, sibling, tree):
        self.sibling_cc = sibling.cc
        sibling.sibling_cc = self.cc
        parent_name = self.name + sibling.name
        self.parent_cc = compute_parent_cc(self.cc)
        tree.add_node(parent_name, self.parent_cc, self.cc, self.sibling_cc)
        if self.is_leaf:
            self.co_path_to_root.append(sibling.exchange_public_keys(self.public_key))

    def exchange_public_keys(self, sibling_pub_key):
        self.co_path_to_root.append(sibling_pub_key)
        return self.public_key

    def compute_next_node_key(self):
        print("i am here doing nothing ---> " + self.name)



