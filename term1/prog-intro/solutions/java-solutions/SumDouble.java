public class SumDouble {
    public static void main(String[] args){
        double summ = 0;
        String s = "";
        int first = -1;
        for (int i = 0; i < args.length; i++){
            for (int j = 0; j < args[i].length(); j++){
                while (j < args[i].length() && !Character.isWhitespace(args[i].charAt(j))){
                    if (first < 0){
                        first = j;
                    } 
                    j++;
                } 
                if (first >= 0){
                    s = args[i].substring(first,j);
                    first = -1;
                }
                if (s.length() > 0){
                    summ += Double.parseDouble(s);
                    s = "";
               } 
            }
        }
        System.out.println(summ);       
    }
}

