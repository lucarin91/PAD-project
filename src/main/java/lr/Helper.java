package lr;

import ie.ucd.murmur.MurmurHash;

/**
 * Created by luca on 04/03/16.
 */
public class Helper {
    static int hash(String s){
        return MurmurHash.hash32(s) /*% 1000*/;
    }
}
