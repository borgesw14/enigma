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
    newKeys = []
    newValues = []

    with open(working_dir + '/resources/data/german/n-gram counts/' + inputfile, 'r') as f:
        data = f.readlines()
        data.reverse()
        keys = []
        values = []

        gerChar = ["Ä", "Ü", "Ö", "ß", "CH"]
        engChar = ["A", "U", "O", "S", "C"]
       

        for j,c in enumerate(gerChar):
            for i, key in enumerate(data):
                data[i] = key.replace(key, re.sub(c, engChar[j], key))

        for i, line in enumerate(data):
            pair = re.split("\s", line)
            keys.append(pair[0])
            values.append(int(pair[1]))
            
        for i, key in enumerate(keys):
            if(key not in newKeys):
                newKeys.append(key)
                newValues.append(values[i])
            else:
                newValues[newKeys.index(key)] = newValues[newKeys.index(key)] + values[i]
        
        res = dict(zip(newKeys, newValues))

    with open(working_dir + '/resources/data/german/' + outputfile, 'w') as w:
        sort = sorted(res.items(), key=lambda x: x[1], reverse=False)

        for i in sort:
            w.write(i[0] + " " + str(i[1]) + "\n")


if __name__ == "__main__":
   main(sys.argv[1:])



