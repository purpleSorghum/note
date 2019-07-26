package top.zigaoliang.util;

import java.util.ArrayList;

public class quickStringMatch
{
    int ASIZE = 1024; // 这个参数可以改，如果要匹配的关键字很多，就使用65536（上界，再多就没用了），否则适当改小。
    boolean bCaseSense = false;
    private int minlen = 9999999; // 最短模式串长度
    private int[] SHIFT_QS = new int[ASIZE]; // 跳转表
    int nKeysNum = 0;
    ArrayList<Integer> pat_len = new ArrayList<Integer>(); // lengths
    ArrayList<ArrayList<Integer>> HASH = new ArrayList<>();
    ArrayList<String> keywords = new ArrayList<String>();
    int m_nCntMtch = 0;

    public quickStringMatch(int asize) {
        this.ASIZE = asize;
        this.SHIFT_QS = new int[ASIZE];
    }

    public quickStringMatch() {}

    public void LoadData(String[] data, boolean bCaseSensitive) {
        if (data.length == 0) {
            return;
        }

        EmptyMem();

        bCaseSense = bCaseSensitive;
        // parse the input string
        int l = 0;
        nKeysNum = data.length;
        while (l < nKeysNum) {
            keywords.add(data[l]);
            pat_len.add(data[l].length());

            if (data[l].length() < minlen) {
                minlen = data[l].length();
            }
            l++;
        }

        make_ready();
        return;
    }

    public class match_Result {
        public int idx_keyword;
        public int idx_context;
    }

    // if nFinish, 如果为-1，表示该参数不起作用。
    public int Match(String sInput, ArrayList<match_Result> pattern, int nFinish)
    {
        if (pattern == null) {
            pattern = new ArrayList<>();
        }

        if (sInput.isEmpty() || nKeysNum == 0) {
            return 0;
        }

        pattern.clear();
        
        int len = sInput.length();
        // begin matching
        int nWindowEnd = minlen - 1;
        boolean bContinue = true;
        while (nWindowEnd < len && bContinue) {
            char ch = sInput.charAt(nWindowEnd);
            if (!bCaseSense){
                ch = Character.toUpperCase(ch);
            }
            ArrayList<Integer> p = HASH.get(ch % ASIZE);

            for (int i = 0; i < p.size(); i++) {
                int nCurPatNum = p.get(i);
                int nCurPatlen = pat_len.get(nCurPatNum);
                String px = keywords.get(nCurPatNum);
                int qx =  nWindowEnd + 1 - minlen;
                int j = 0;

                // case sensitive
                if (bCaseSense) {
                    while ( j < nCurPatlen &&  px.charAt(j) == sInput.charAt(qx)) {
                        j++;
                        qx++;
                    }
                }
                else // case-less
                {
                    while (j < nCurPatlen && Character.toUpperCase(px.charAt(j)) == Character.toUpperCase(sInput.charAt(qx))) {
                        j++;
                        qx++;
                    }
                }

                if (nCurPatlen <= j) // matched
                {
                    match_Result rlt = new match_Result();
                    rlt.idx_context = nWindowEnd;
                    rlt.idx_keyword = nCurPatNum;
                    pattern.add(rlt);

                    if (nCurPatNum == nFinish){
                        bContinue = false;
                        break;
                    }
                }
            }

            // jumping
            if (nWindowEnd+1 < len){
                ch = sInput.charAt(nWindowEnd+1);
            }
            else
            { // finished
                break;
            }


            if (!bCaseSense){
                ch = Character.toUpperCase(ch);
            }
            nWindowEnd += SHIFT_QS[ch % ASIZE];
        }

        return pattern.size();
    }


    private void EmptyMem(){
        for (int i = 0; i < ASIZE; i++) {
            SHIFT_QS[i] = 0;
        }

        HASH.clear();
        keywords.clear();
        m_nCntMtch = 0;
    }

    private void make_ready()
    {
        if (keywords.size() == 0) {
            return;
        }

        // init SHIFT_QS table
        for (int i = 0; i < ASIZE; i++) {
            SHIFT_QS[i] = minlen + 1;
        }

        // init HASH table
        HASH.ensureCapacity(ASIZE);
        for (int i = 0; i < ASIZE; i++) {
            HASH.add(new ArrayList<>());
        }

        for (int i = 0; i < nKeysNum; i++) {
            Pat_Preprocess(i);
        }
    }

    /*
     *	to Pre-process each single pattern. called by Load_Patterns.
     */
    private void Pat_Preprocess(int pat_index) {
        if (!bCaseSense){
            keywords.get(pat_index).toUpperCase();
        }

        // make skip table
        String str = keywords.get(pat_index);
        int ch = 0;
        for (int i = 0; i < minlen; i++) {
            ch = str.charAt(i)%ASIZE;
            SHIFT_QS[ch] = Math.min(minlen - i, SHIFT_QS[ch]);
        }

        // make HASH table
        ch  = str.charAt(minlen - 1)%ASIZE;
        HASH.get(ch).add(pat_index);
    }
}

