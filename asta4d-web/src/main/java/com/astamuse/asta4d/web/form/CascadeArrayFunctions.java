package com.astamuse.asta4d.web.form;


public interface CascadeArrayFunctions {

    public static final int[] EMPTY_INDEXES = new int[0];

    /**
     * Sub classes can override this method to supply a customized array index placeholder mechanism.
     * 
     * @param s
     * @param indexes
     * @return
     */
    default String rewriteArrayIndexPlaceHolder(String s, int[] indexes) {
        String ret = s;
        for (int i = indexes.length - 1; i >= 0; i--) {
            ret = CascadeArrayFunctionsHelper.PlaceHolderSearchPattern[i].matcher(ret).replaceAll("$1\\" + indexes[i] + "$3");
        }
        return ret;
    }
}
