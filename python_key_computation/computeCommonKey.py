import os

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
from ecdsa import NIST256p, SigningKey, VerifyingKey
from ecdsa.ellipticcurve import Point

import node_class

name_d = {
    0: "A",
    1: "B",
    2: "C",
    3: "D"
}


def read_log_file(filename):
    keys = []
    dids = []
    with open(filename) as f:
        lines = f.readlines()
        for i in range(len(lines)):
            random_count = lines[i].count("random")
            register_count = lines[i].count("register")
            if random_count + register_count > 0:
                did = get_did(lines[i])
                pub_key = get_pub_key(lines[i])
                keys.append(create_public_key(int(pub_key[0]), int(pub_key[1])))
                dids.append(did)
    # print(keys)
    return keys, dids


def get_did(line):
    token = ""
    if "did:hlf:" in line:
        token = "did:hlf:"
    elif "random:hlf:" in line:
        token = "random:hlf:"
    index = line.index(token)
    return line[index:index + len(token) + 64]


def get_pub_key(line):
    token_start = "publicxcoord"
    index_start = line.index(token_start)
    token_end = "parameters:secp256r1[NISTP-256,X9.62prime256v1](1.2.840.10045.3.1.7)"
    index_end = line.index(token_end)
    return line[index_start:index_end + len(token_end)].replace("publicxcoord:", "").replace("publicycoord:",
                                                                                             "").replace("parameters:",
                                                                                                         "").split(
        "\\n")


def get_private_key_from_path(number):
    directory = os.getcwd()
    file_path = os.path.join(directory, "org" + number, "priv_sk")

    with open(file_path, 'rb') as file:
        private_key_pem = file.read()

    private_key_cryptography = serialization.load_pem_private_key(
        private_key_pem,
        password=None,
        backend=default_backend()
    )

    private_number = private_key_cryptography.private_numbers().private_value
    private_key_ecdsa = SigningKey.from_secret_exponent(private_number, curve=NIST256p)
    return private_key_ecdsa


def create_public_key(x, y):
    curve = NIST256p
    point = Point(curve.curve, x, y)
    public_key_ecdsa = VerifyingKey.from_public_point(point, curve)
    return public_key_ecdsa

def create_public_key_from_string(string):
    curve = NIST256p
    public_key_ecdsa = VerifyingKey.from_string(string, curve)
    return public_key_ecdsa


def init_nodes(keys_, dids_, private_key, intOrgNumber):
    nodes = []
    for i in range(len(keys_)):
        if i == intOrgNumber:
            nodes.append(node_class.Node(name_d[i], did=dids_[i], public_info=keys_[i], private_info=private_key, final=True))
        else:
            nodes.append(node_class.Node(name_d[i], did=dids_[i], public_info=keys_[i], final=True))

    for i in range(0, len(nodes), 2):
        nodes[i].setSibling(nodes[i + 1], siblingPublicKey=nodes[i + 1].public_key, hasKey=True)
        nodes[i + 1].setSibling(nodes[i], siblingPublicKey=nodes[i].public_key, hasKey=True)

    return nodes


def create_parents(tree_nodes):
    level = 0
    i = len(tree_nodes[0])
    while i > 1:
        # print("i --> ", i)
        parents_ = []
        for j in range(0, i, 2):
            l = tree_nodes[level][j]
            r = tree_nodes[level][j + 1]
            parents_.append(node_class.Node(l.name + r.name, final=False, left=l, right=r))

        # print("#parents --> ", len(parents_))
        if len(parents_) > 1:
            for j in range(0, len(parents_), 2):
                parents_[j].setSibling(parents_[j + 1], hasKey=False)
                parents_[j + 1].setSibling(parents_[j], hasKey=False)
        level = level + 1

        tree_nodes[level] = parents_

        i = int(i / 2)
    # print(tree_nodes)
    return tree_nodes


def compute_global_key(tree_nodes, index_private, topic, start=0):
    me = tree_nodes[start][index_private]
    me.computeParentKey(topic)
    parent = me.getParent()
    parent.computeParentKey(topic)
    g_parent = parent.getParent()
    g_parent.computeParentKey(topic)



def main(orgNumber, index, topic):
    file_ = ["logfile_", orgNumber, ".log"]
    name = "".join(file_)
    keys_, dids_ = read_log_file(name)
    private_key = get_private_key_from_path(orgNumber)
    intOrgNumber = int(orgNumber)
    if intOrgNumber < 2:
        intOrgNumber = 0
    nodes = init_nodes(keys_, dids_, private_key, intOrgNumber)
    tree_nodes = {
        0: nodes,
    }
    tree_nodes = create_parents(tree_nodes)
    # print(tree_nodes[2][0].name)
    # print(tree_nodes)

    print(tree_nodes)

    for k in tree_nodes.keys():
        nodes = tree_nodes[k]
        for node in nodes:
            print("NAME: ", node.name, " --  SIBLING NAME: ", node.getSiblingName(), " --  PARENT NAME: ",
                  node.getPArentName())
    compute_global_key(tree_nodes, index, topic, start=0)
    # tree_nodes[0][0].computeParentKey()



# main("1")
# print()
#main("1", 0, "my_topic_key_setup")
main("2", 2, "my_topic_key_setup")



#
