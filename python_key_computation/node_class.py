import base64
import hashlib

from ecdsa import NIST256p, SigningKey, VerifyingKey
from ecdsa.ellipticcurve import Point

import kafka_handler


def create_public_key_from_string(string):
    curve = NIST256p
    public_key_ecdsa = VerifyingKey.from_string(string, curve)
    return public_key_ecdsa


class Node:
    def __init__(self, name, did=None, public_info=False, private_info=None, final=True, left=None, right=None):
        self.name = name
        self.did = did
        self.sibling = None
        self.sibling_b = False
        self.siblingName = ""
        self.parent = None
        self.parent_b = False
        self.public_key = public_info
        self.__private_info = private_info
        self.siblingPublicKey = None
        self.siblingPublicKey_b = False
        self.leaf = final
        self.left = left
        self.right = right
        self.parentName = ""
        if not final:
            self.left.setParent(self, self.name)
            self.right.setParent(self, self.name)

    def computeParentKey(self, topic):
        print("me  -> ", self.did, "   ---   ", self.name)
        if self.sibling_b and self.parent_b and self.siblingPublicKey_b:
            privatePart = self.__private_info
            publicPart = self.siblingPublicKey
            curve = NIST256p
            x = publicPart.pubkey.point.x()
            y = publicPart.pubkey.point.y()
            point = Point(curve.curve, x, y)
            private = privatePart.privkey.secret_multiplier
            shared_secret_point = private * point
            shared_secret = shared_secret_point.x()
            parent_private_key = SigningKey.from_secret_exponent(shared_secret, curve=NIST256p)
            parent_public_key = parent_private_key.get_verifying_key()
            self.parent.setKeyPairs(parent_private_key, parent_public_key, self)
        elif self.sibling_b and self.parent_b and not self.siblingPublicKey_b:
            # has sibling but not its public key
            # print("x -> ",self.public_key.pubkey.point.x())
            # print("y -> ",self.public_key.pubkey.point.y())
            # print("self key ->", self.public_key)
            # print("self key to string ->", self.public_key.to_string())
            message_encoded = kafka_handler.handle_message_send(self.did, self.public_key.to_string())
            # print("SENDING -- > ", message_encoded)
            kafka_handler.main_producer(message_encoded, topic)
            pub_sibling = kafka_handler.main_consumer(self.did, topic)
            # print("PUB SIBLING --> ", pub_sibling)
            pub_key_sibling = create_public_key_from_string(pub_sibling)
            self.siblingPublicKey = pub_key_sibling
            self.siblingPublicKey_b = True
            
            print()
            print("HAVE KEY FROM SIBLING")
            print()
            self.computeParentKey(topic)
            # print("PUB KEY SIBLING --> ", pub_key_sibling)
            # print("x' -> ",pub_key_sibling.pubkey.point.x())
            # print("y' -> ",pub_key_sibling.pubkey.point.y())
            # public_reencoded = base64.b64decode(pub_sibling)
            """
            final = VerifyingKey.from_string(public_reencoded, curve=NIST256p)
            self.siblingPublicKey = final
            self.siblingPublicKey_b = True
            self.computeParentKey(topic_1, topic_2)
            """

            """
            print("1 --> ", self.public_key)
            public_decoded = base64.b64encode(self.public_key.to_string()).decode('utf-8')
            print("2 --> ", public_decoded)
            public_reencoded = base64.b64decode(public_decoded)
            print("3 --> ", public_decoded)
            dk = VerifyingKey.from_string(public_reencoded, curve=NIST256p)
            print("4 --> ", dk)
            """

        elif not self.sibling_b:
            print("COMMON KEY HERE")
            print(self.__private_info)
            print(self.__private_info.privkey)
            print(self.__private_info.privkey.secret_multiplier)

        else:
            return False

    def setSibling(self, node, siblingPublicKey=None, hasKey=False):
        self.siblingName = node.name
        self.sibling = node
        self.sibling_b = True
        self.siblingPublicKey = siblingPublicKey
        self.siblingPublicKey_b = hasKey

    def getPrivateKey(self, node):
        if node.getSibling == self:
            return self.__private_info
        return False

    def getSibling(self):
        return self.sibling

    def getSiblingName(self):
        return self.siblingName

    def setParent(self, node, name):
        self.parent = node
        self.parentName = name
        self.parent_b = True

    def getParent(self):
        return self.parent

    def setKeyPairs(self, private_key, public_key, node):
        if node == self.left or node == self.right:
            print("here, setting my key --> ", self.name)
            print("-->", private_key)
            self.__private_info = private_key
            self.public_key = public_key
            self.compute_did()

    def getPArentName(self):
        return self.parentName

    def compute_did(self):
        public_key = self.public_key
        public_key_str = public_key.to_string()
        public_key_str_base64 = base64.b64encode(public_key_str).decode()
        # Compute the SHA-256 hash of the base64 encoded public key PEM
        hash_object = hashlib.sha256(public_key_str_base64.encode('utf-8'))
        hex_dig = hash_object.hexdigest()
        did = "did:hlf:" + hex_dig
        self.did = did
