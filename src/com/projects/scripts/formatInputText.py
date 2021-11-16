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
    output = ""

    with open(working_dir + "/" +  inputfile, 'r') as f:
        data = f.readlines()
        """ orgChar = ["Ä", "Ü", "Ö", "ß", ".", ":", "CH", "?", ",", "-", "/", "\(", "\)"] # original char
        repChar = ["A", "U", "O", "S", "X", "XX", "C", "UD", "Y", "YY", "YY", "KK", "KK"] # replacement char """

        orgChar = ["Ä", "Ü", "Ö", "ß", "\.", ":", "CH", "\?", ",", "-", "/", "\(", "\)", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"] # original char
        repChar = ["A", "U", "O", "S", "X", "XX", "C", "UD", "Y", "YY", "YY", "KK", "KK", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "ZERO"] # replacement char

        # individual char replacement 
        for j,c in enumerate(orgChar):
            for i, key in enumerate(data):
                data[i] = key.replace(key, re.sub(c, repChar[j], key))
                data[i] = data[i].upper()
        
        for i, key in enumerate(data):
            output += data[i]
        


    with open(working_dir + "/" + outputfile, 'w') as w:
        w.write(output)



if __name__ == "__main__":
   main(sys.argv[1:])