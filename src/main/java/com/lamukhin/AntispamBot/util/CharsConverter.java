package com.lamukhin.AntispamBot.util;

import java.util.HashMap;

public class CharsConverter {
    HashMap<Character, String> mapping = new HashMap<>();
mapping.put('\u0410',"A");
mapping.put('\u0412',"B");
mapping.put('\u0421',"C");
mapping.put('\u0415',"E");
mapping.put('\u041D',"H");
mapping.put('\u041A',"K");
mapping.put('\u041C',"M");
mapping.put('\u041E',"O");
mapping.put('\u0420',"P");
mapping.put('\u0422',"T");
mapping.put('\u0423',"Y");
mapping.put('\u0425',"X");

    //А, Е, К, М, О, С, Т, В, Н, Р, Х, Y
    //a e k m o c t b h p x y

}
