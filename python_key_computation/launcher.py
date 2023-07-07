from classes import BinaryTree

A_ = ["A", "4606e039b9caa6afa8dd40f06014a71294c049021f98b98c2275b5df750e779e"]
B_ = ["B", "30aa3a42324342afc9645472eb5a5bb3c6acc6d35e393e6ed2dae80dbe6fe5d5"]
C_ = ["C", "a07996627ab8d45597fa8dd94622c3cc3142954598865e12d12f0b69194b2af1"]
D_ = ["D", "2203f39a2b85630a388e0292775eda35ccc0b369c64818814d35219263077ab9"]

tree = BinaryTree()

Current = A_
tree.add_leaf(name=Current[0], _private_key_hex=Current[1])

Current = B_
tree.add_leaf(name=Current[0], _private_key_hex=Current[1])

Current = C_
tree.add_leaf(name=Current[0], _private_key_hex=Current[1])


Current = D_
tree.add_leaf(name=Current[0], _private_key_hex=Current[1])


print(tree.nodes.keys())
