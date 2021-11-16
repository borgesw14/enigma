import math
import os, re, sys, getopt

def main(argv):
    inputfile = '/trigram.txt'
    outputfile = '/trigram'
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
    newKeys = []
    newValues = []

    with open(working_dir + inputfile, 'r') as f:
        data = f.readlines()
        data.reverse()
        keys = []
        values = []

        gerChar = ["Ä", "Ü", "Ö", "ß", "CH"]
        engChar = ["A", "U", "O", "S", "C"]

        for line in data:
            x = re.split("\s", line)

            keys.append(x[0])
            values.append(x[1])

    total = 0

    for i in range(len(keys)):
        total += int(values[i])


    for i in range(len(values)):
        probability  = int(values[i])/total
        logProb = math.log((int(values[i])/total), 10) #must be base 10, default base is e
        newValues.append(logProb)


    res = {keys[i]: newValues[i] for i in range(len(keys))}


    with open(working_dir + outputfile, 'w') as w:
        sort = sorted(res.items(), key=lambda x: x[1], reverse=True)
        for i in sort:
            w.write(i[0] + "," + str(i[1]) + "\n")

    w.close()

if __name__ == "__main__":
   main(sys.argv[1:])

