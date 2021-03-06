import os, re, sys, getopt

def main(argv):
    inputfile = ''
    outputfile = ''
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
    except getopt.GetoptError:
        print('test.py -i <inputfile> -o <outputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print ('test.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg
   
    working_dir = os.getcwd()

    with open(working_dir + '/data/german/n-gram counts/' + inputfile, 'r') as f:
        data = f.readlines()

        for line in data:
            x = re.search('L[ÄA][UÜ]', line)

            if(x != None):
                print(line)


if __name__ == "__main__":
   main(sys.argv[1:])