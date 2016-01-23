package com.nlpir;
public class PorterStemmer
    {
        private char[] b;
        private int i,    /* offset into b */
            j, k, k0;
        private boolean dirty = false;
        private static int INC = 50; /* unit of size whereby b is increased */
        private static int EXTRA = 1;
 
        ///
        /// Initializes a new instance of the PorterStemmer class.
        ///
        public PorterStemmer()
        {
            b = new char[INC];
            i = 0;
        }
 
        ///
        /// reset() resets the stemmer so it can stem another word.  If you invoke
        /// the stemmer by calling add(char) and then stem(), you must call reset()
        /// before starting another word.
        ///
        public void reset() { i = 0; dirty = false; }
 
        ///
        /// Add a character to the word being stemmed.  When you are finished
        /// adding characters, you can call stem(void) to process the word.
        ///
        public void add(char ch)
        {
            if (b.length <= i + EXTRA)
            {
                char[] new_b = new char[b.length + INC];
                for (int c = 0; c < b.length; c++)
                    new_b[c] = b[c];
                b = new_b;
            }
            b[i++] = ch;
        }
        
       
        /**
         * 增加wLen长度的字符数组到存放待处理的单词的数组b。
         * @param w
         * @param wLen
         */
        public void add(char[] w, int wLen)
        {  if (i+wLen >= b.length)
        {  char[] new_b = new char[i+wLen+INC];
        for (int c = 0; c < i; c++) new_b[c] = b[c];
        b = new_b;
        }
        for (int c = 0; c < wLen; c++) b[i++] = w[c];
        }
 
        ///
        /// After a word has been stemmed, it can be retrieved by toString(),
        /// or a reference to the internal buffer can be retrieved by getResultBuffer
        /// and getResultLength (which is generally more efficient.)
        ///
        public String toString() { return new String(b, 0, i); }
 
        ///
        /// Returns the length of the word resulting from the stemming process.
        ///
        public int getResultLength() { return i; }
 
        ///
        /// Returns a reference to a character buffer containing the results of
        /// the stemming process.  You also need to consult getResultLength()
        /// to determine the length of the result.
        ///
        public char[] getResultBuffer() { return b; }
 
        ///
        /// cons(i) is true <=> b[i] is a consonant.
        /// cons(i) 为真 <=> b[i] 是一个辅音
        ///
        /// The input parameter.
        /// True or False.
        private boolean cons(int i)
        {
            switch (b[i])
            {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                    return false;
                case 'y':
                    return (i == k0) ? true : !cons(i - 1);//y开头，为辅；否则看i-1位，如果i-1位为辅，y为元，反之亦然。
                default:
                    return true;
            }
        }
 
        ///
        /// m() 用来计算在0和j之间辅音序列的个数
        /// m() measures the number of consonant sequences between k0 and j. if c is
        ///   a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
        ///   presence,
        ///
        ///            gives 0
        ///     vc     gives 1
        ///     vcvc   gives 2
        ///     vcvcvc gives 3
        ///     ....
        ///
        ///
        private int m()
        {
            int n = 0;//辅音序列的个数，初始化
            int i = k0;//偏移量
            while (true)
            {
                if (i > j)//如果超出最大偏移量，直接返回n
                    return n;
                if (!cons(i))//如果是元音，中断
                    break;
                i++;//辅音移一位，直到元音的位置
            }
            i++;//移完辅音，从元音的第一个字符开始
            while (true)//循环计算vc的个数
            {
                while (true)//循环判断v
                {
                    if (i > j)
                        return n;
                    if (cons(i))
                        break;//出现辅音则终止循环
                    i++;
                }
                i++;
                n++;
                while (true)//循环判断c
                {
                    if (i > j)
                        return n;
                    if (!cons(i))
                        break;
                    i++;
                }
                i++;
            }
        }
 
        ///
        /// vowelinstem() is true <=> k0,...j contains a vowel
        ///  vowelinstem() 为真 <=> 0,...j 包含一个元音
        ///
        /// [To be supplied.]
        private boolean vowelinstem()
        {
            int i;
            for (i = k0; i <= j; i++)
                if (!cons(i))
                    return true;
            return false;
        }
 
        ///
        /// doublec(j) is true <=> j,(j-1) contain a double consonant.
        ///  doublec(j) 为真 <=> j,(j-1) 包含两个一样的辅音
        ///
        ///
        ///
        private boolean doublec(int j)
        {
            if (j < k0 + 1)
                return false;
            if (b[j] != b[j - 1])
                return false;
            return cons(j);
        }
 
        /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
           and also if the second c is not w,x or y. this is used when trying to
           restore an e at the end of a short word. e.g.
 
                cav(e), lov(e), hop(e), crim(e), but
                snow, box, tray.
 
        */
        /* cvc(i) is 为真 <=> i-2,i-1,i
         * 有形式： 辅音 - 元音 - 辅音  
         * 并且第二个c不是 w,x 或者 y.
         * 这个用来处理以e结尾的短单词。
         * e.g.      cav(e), lov(e), hop(e), crim(e),
         * 但不是    snow, box, tray.   */
        private boolean cvc(int i)
        {
            if (i < k0 + 2 || !cons(i) || cons(i - 1) || !cons(i - 2))
                return false;
            else
            {
                int ch = b[i];
                if (ch == 'w' || ch == 'x' || ch == 'y') return false;
            }
            return true;
        }
 
        private boolean ends(String s)
        {
            int l = s.length();
            int o = k - l + 1;
            if (o < k0)
                return false;
            for (int i = 0; i < l; i++)
                if (b[o + i] != s.charAt(i))
                    return false;
            j = k - l;
            return true;
        }
 
        /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
           k. */
        // setto(s) 设置 (j+1),...k 到s字符串上的字符, 并且调整k值
        void setto(String s)
        {
            int l = s.length();
            int o = j + 1;
            for (int i = 0; i < l; i++)
                b[o + i] = s.charAt(i);
            k = j + l;
            dirty = true;
        }
 
        /* r(s) is used further down. */
 
        void r(String s) { if (m() > 0) setto(s); }
 
        /* step1() gets rid of plurals and -ed or -ing. e.g.
          处理复数，ed或者ing结束的单词。比如：
 
                 caresses  ->  caress
                 ponies    ->  poni
                 ties      ->  ti
                 caress    ->  caress
                 cats      ->  cat
 
                 feed      ->  feed
                 agreed    ->  agree
                 disabled  ->  disable
 
                 matting   ->  mat
                 mating    ->  mate
                 meeting   ->  meet
                 milling   ->  mill
                 messing   ->  mess
 
                 meetings  ->  meet
 
        */
 
        private void step1()
        {
            if (b[k] == 's')
            {
                if (ends("sses")) k -= 2;//以“sses结尾”
                else if (ends("ies")) setto("i");//以ies结尾，置为i
                else if (b[k - 1] != 's') k--;//两个s结尾不处理
            }
            if (ends("eed"))//以“eed”结尾，当m>0时，左移一位
            {
                if (m() > 0)
                    k--;
            }
            else if ((ends("ed") || ends("ing")) && vowelinstem())
            {
                k = j;
                if (ends("at")) setto("ate");
                else if (ends("bl")) setto("ble");
                else if (ends("iz")) setto("ize");
                else if (doublec(k))//如果有两个相同辅音
                {
                    int ch = b[k--];
                    if (ch == 'l' || ch == 's' || ch == 'z')
                        k++;
                }
                else if (m() == 1 && cvc(k))
                    setto("e");
            }
        }
 
        /* step2() turns terminal y to i when there is another vowel in the stem. */
        //如果单词中包含元音，并且以y结尾，将y改为i
        private void step2()
        {
            if (ends("y") && vowelinstem())
            {
                b[k] = 'i';
                dirty = true;
            }
        }
 
        /* step3() maps double suffices to single ones. so -ization ( = -ize plus
           -ation) maps to -ize etc. note that the string before the suffix must give
           m() > 0. */
        /* step3() 将双后缀的单词映射为单后缀。
         * 所以 -ization ( = -ize 加上    -ation) 被映射到 -ize 等等。
         * 注意在去除后缀之前必须确保    m() > 0. */
        private void step3()
        {
            if (k == k0) return; /* For Bug 1 */
            switch (b[k - 1])
            {
                case 'a':
                    if (ends("ational")) { r("ate"); break; }
                    if (ends("tional")) { r("tion"); break; }
                    break;
                case 'c':
                    if (ends("enci")) { r("ence"); break; }
                    if (ends("anci")) { r("ance"); break; }
                    break;
                case 'e':
                    if (ends("izer")) { r("ize"); break; }
                    break;
                case 'l':
                    if (ends("bli")) { r("ble"); break; }
                    if (ends("alli")) { r("al"); break; }
                    if (ends("entli")) { r("ent"); break; }
                    if (ends("eli")) { r("e"); break; }
                    if (ends("ousli")) { r("ous"); break; }
                    break;
                case 'o':
                    if (ends("ization")) { r("ize"); break; }
                    if (ends("ation")) { r("ate"); break; }
                    if (ends("ator")) { r("ate"); break; }
                    break;
                case 's':
                    if (ends("alism")) { r("al"); break; }
                    if (ends("iveness")) { r("ive"); break; }
                    if (ends("fulness")) { r("ful"); break; }
                    if (ends("ousness")) { r("ous"); break; }
                    break;
                case 't':
                    if (ends("aliti")) { r("al"); break; }
                    if (ends("iviti")) { r("ive"); break; }
                    if (ends("biliti")) { r("ble"); break; }
                    break;
                case 'g':
                    if (ends("logi")) { r("log"); break; }
                    break;
            }
        }
 
        /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
        //处理-ic-，-full，-ness等等后缀。和步骤3有着类似的处理。
        private void step4()
        {
            switch (b[k])
            {
                case 'e':
                    if (ends("icate")) { r("ic"); break; }
                    if (ends("ative")) { r(""); break; }
                    if (ends("alize")) { r("al"); break; }
                    break;
                case 'i':
                    if (ends("iciti")) { r("ic"); break; }
                    break;
                case 'l':
                    if (ends("ical")) { r("ic"); break; }
                    if (ends("ful")) { r(""); break; }
                    break;
                case 's':
                    if (ends("ness")) { r(""); break; }
                    break;
            }
        }
 
        /* step5() takes off -ant, -ence etc., in context vcvc. */
        //在vcvc情形下，去除-ant，-ence等后缀。
        private void step5()
        {
            if (k == k0) return; /* for Bug 1 */
            switch (b[k - 1])
            {
                case 'a':
                    if (ends("al")) break;
                    return;
                case 'c':
                    if (ends("ance")) break;
                    if (ends("ence")) break;
                    return;
                case 'e':
                    if (ends("er")) break; return;
                case 'i':
                    if (ends("ic")) break; return;
                case 'l':
                    if (ends("able")) break;
                    if (ends("ible")) break; return;
                case 'n':
                    if (ends("ant")) break;
                    if (ends("ement")) break;
                    if (ends("ment")) break;
                    /* element etc. not stripped before the m */
                    if (ends("ent")) break;
                    return;
                case 'o':
                    if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                    /* j >= 0 fixes Bug 2 */
                    if (ends("ou")) break;
                    return;
                /* takes care of -ous */
                case 's':
                    if (ends("ism")) break;
                    return;
                case 't':
                    if (ends("ate")) break;
                    if (ends("iti")) break;
                    return;
                case 'u':
                    if (ends("ous")) break;
                    return;
                case 'v':
                    if (ends("ive")) break;
                    return;
                case 'z':
                    if (ends("ize")) break;
                    return;
                default:
                    return;
            }
            if (m() > 1)
                k = j;
        }
 
        // step6() removes a final -e if m() > 1.
        //也就是最后一步，在m()>1的情况下，移除末尾的“e”。
        private void step6()
        {
            j = k;
            if (b[k] == 'e')
            {
                int a = m();
                if (a > 1 || a == 1 && !cvc(k - 1))
                    k--;
            }
            if (b[k] == 'l' && doublec(k) && m() > 1)
                k--;
        }
 
 
        ///
        /// Stem a word provided as a string.  Returns the result as a string.
        ///
        public String stem(String s)
        {
            if (stem(s.toCharArray(), s.length()))
                return toString();
            else
                return s;
        }
 
        /// Stem a word contained in a char[].  Returns true if the stemming process
        /// resulted in a word different from the input.  You can retrieve the
        /// result with getResultLength()/getResultBuffer() or toString().
        ///
        public boolean stem(char[] word)
        {
            return stem(word, word.length);
        }
 
        /// Stem a word contained in a portion of a char[] array.  Returns
        /// true if the stemming process resulted in a word different from
        /// the input.  You can retrieve the result with
        /// getResultLength()/getResultBuffer() or toString(). 
        ///
        public boolean stem(char[] wordBuffer, int offset, int wordLen)
        {
            reset();
            if (b.length < wordLen)
            {
                char[] new_b = new char[wordLen + EXTRA];
                b = new_b;
            }
            for (int j = 0; j < wordLen; j++)
                b[j] = wordBuffer[offset + j];
            i = wordLen;
            return stem(0);
        }
 
        /// Stem a word contained in a leading portion of a char[] array.
        /// Returns true if the stemming process resulted in a word different
        /// from the input.  You can retrieve the result with
        /// getResultLength()/getResultBuffer() or toString(). 
        ///
        public boolean stem(char[] word, int wordLen)
        {
            return stem(word, 0, wordLen);
        }
 
        /// Stem the word placed into the Stemmer buffer through calls to add().
        /// Returns true if the stemming process resulted in a word different
        /// from the input.  You can retrieve the result with
        /// getResultLength()/getResultBuffer() or toString(). 
        ///
        public boolean stem()
        {
            return stem(0);
        }
 
        ///
        /// [To be supplied.]
        ///
        /// [To be supplied.]
        /// [To be supplied.]
        public boolean stem(int i0)
        {
            k = i - 1;
            k0 = i0;
            if (k > k0 + 1)
            {
                step1(); step2(); step3(); step4(); step5(); step6();
            }
            // Also, a word is considered dirty if we lopped off letters
            // Thanks to Ifigenia Vairelles for pointing this out.
            if (i != k + 1)
                dirty = true;
            i = k + 1;
            return dirty;
        }
 
        
        /// Test program for demonstrating the Stemmer.  It reads a file and
        /// stems each word, writing the result to standard out. 
        /// Usage: Stemmer file-name
        ///
        public static void main(String[] args)
        {
        	/* 传入的单词是小写，且传入下一个单词前s.reset();   */
            PorterStemmer s = new PorterStemmer();
            String str = "imposed";
            s.add(str.toCharArray(),str.length());
            s.stem();
           
            System.out.println(s.toString());
            
            s.reset();
            
            str = "candidates";
            s.add(str.toCharArray(),str.length());
            s.stem();
            System.out.println(s.toString());
            
            
        }
    }