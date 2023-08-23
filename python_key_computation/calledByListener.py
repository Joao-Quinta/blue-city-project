import logging
import sys


def main(arg1, arg2):
    logging.basicConfig(filename=arg1, level=logging.INFO)
    logging.info(arg2)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
