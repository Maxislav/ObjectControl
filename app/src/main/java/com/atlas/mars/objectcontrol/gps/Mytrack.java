package com.atlas.mars.objectcontrol.gps;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Администратор on 6/14/15.
 */
public class Mytrack {
    //static String TRACK = "{\"data\":{\"trackData\":[[{\"lon\":30.448329448699948,\"lat\":50.446013695075955,\"ele\":155},{\"lon\":30.44839,\"lat\":50.44602,\"ele\":155},{\"lon\":30.44854,\"lat\":50.44577,\"ele\":154},{\"lon\":30.44859,\"lat\":50.44567,\"ele\":154},{\"lon\":30.4436,\"lat\":50.44439,\"ele\":155},{\"lon\":30.44278,\"lat\":50.44421,\"ele\":154},{\"lon\":30.44109,\"lat\":50.44378,\"ele\":158},{\"lon\":30.44085,\"lat\":50.44375,\"ele\":159},{\"lon\":30.44074,\"lat\":50.44376,\"ele\":159},{\"lon\":30.44064,\"lat\":50.44378,\"ele\":160},{\"lon\":30.44055,\"lat\":50.44383,\"ele\":161},{\"lon\":30.4405,\"lat\":50.44389,\"ele\":161},{\"lon\":30.44048,\"lat\":50.44396,\"ele\":162},{\"lon\":30.44049,\"lat\":50.44403,\"ele\":162},{\"lon\":30.44055,\"lat\":50.44409,\"ele\":162},{\"lon\":30.44063,\"lat\":50.44414,\"ele\":162},{\"lon\":30.44155,\"lat\":50.44438,\"ele\":157},{\"lon\":30.44174,\"lat\":50.44437,\"ele\":156},{\"lon\":30.44187,\"lat\":50.44431,\"ele\":156},{\"lon\":30.44201,\"lat\":50.44418,\"ele\":155},{\"lon\":30.44214,\"lat\":50.44361,\"ele\":155},{\"lon\":30.44239,\"lat\":50.44313,\"ele\":156},{\"lon\":30.44315,\"lat\":50.4419,\"ele\":156},{\"lon\":30.44395,\"lat\":50.44073,\"ele\":149},{\"lon\":30.44427,\"lat\":50.44036,\"ele\":150},{\"lon\":30.44452,\"lat\":50.44013,\"ele\":151},{\"lon\":30.44486,\"lat\":50.43988,\"ele\":152},{\"lon\":30.44543,\"lat\":50.43953,\"ele\":156},{\"lon\":30.44616,\"lat\":50.43917,\"ele\":158},{\"lon\":30.44686,\"lat\":50.43892,\"ele\":161},{\"lon\":30.44755,\"lat\":50.43871,\"ele\":161},{\"lon\":30.45039,\"lat\":50.43793,\"ele\":161},{\"lon\":30.4519,\"lat\":50.43749,\"ele\":166},{\"lon\":30.45209,\"lat\":50.43742,\"ele\":168},{\"lon\":30.4523,\"lat\":50.43731,\"ele\":170},{\"lon\":30.45244,\"lat\":50.4372,\"ele\":171},{\"lon\":30.45257,\"lat\":50.43708,\"ele\":172},{\"lon\":30.45284,\"lat\":50.43663,\"ele\":175},{\"lon\":30.45392,\"lat\":50.43421,\"ele\":180},{\"lon\":30.45236,\"lat\":50.43201,\"ele\":183},{\"lon\":30.45133,\"lat\":50.4306,\"ele\":182},{\"lon\":30.45367,\"lat\":50.42993,\"ele\":181},{\"lon\":30.45395,\"lat\":50.42987,\"ele\":181},{\"lon\":30.45544,\"lat\":50.42783,\"ele\":183},{\"lon\":30.45685,\"lat\":50.42602,\"ele\":186},{\"lon\":30.45604,\"lat\":50.42573,\"ele\":187},{\"lon\":30.45872,\"lat\":50.42494,\"ele\":184},{\"lon\":30.45805,\"lat\":50.42401,\"ele\":184},{\"lon\":30.45802,\"lat\":50.42389,\"ele\":184},{\"lon\":30.45809,\"lat\":50.42379,\"ele\":184},{\"lon\":30.45821,\"lat\":50.42369,\"ele\":184},{\"lon\":30.45835,\"lat\":50.4236,\"ele\":183},{\"lon\":30.45842,\"lat\":50.42353,\"ele\":183},{\"lon\":30.45843,\"lat\":50.42344,\"ele\":183},{\"lon\":30.45839,\"lat\":50.42329,\"ele\":183},{\"lon\":30.4572,\"lat\":50.42107,\"ele\":184},{\"lon\":30.45532,\"lat\":50.41742,\"ele\":186},{\"lon\":30.45529,\"lat\":50.4173,\"ele\":186},{\"lon\":30.4552,\"lat\":50.41618,\"ele\":185},{\"lon\":30.46106,\"lat\":50.41574,\"ele\":176},{\"lon\":30.46448,\"lat\":50.41537,\"ele\":176},{\"lon\":30.46419,\"lat\":50.41376,\"ele\":175},{\"lon\":30.46438,\"lat\":50.41375,\"ele\":175},{\"lon\":30.46517,\"lat\":50.41376,\"ele\":173},{\"lon\":30.46705,\"lat\":50.41368,\"ele\":170},{\"lon\":30.46733,\"lat\":50.41361,\"ele\":169},{\"lon\":30.46883,\"lat\":50.41304,\"ele\":160},{\"lon\":30.46946,\"lat\":50.41283,\"ele\":155},{\"lon\":30.46999,\"lat\":50.41272,\"ele\":154},{\"lon\":30.47052,\"lat\":50.41263,\"ele\":153},{\"lon\":30.47166,\"lat\":50.4125,\"ele\":152},{\"lon\":30.47196,\"lat\":50.41244,\"ele\":150},{\"lon\":30.47261,\"lat\":50.41219,\"ele\":148},{\"lon\":30.47473,\"lat\":50.41132,\"ele\":144},{\"lon\":30.4751,\"lat\":50.41107,\"ele\":144},{\"lon\":30.4753,\"lat\":50.41099,\"ele\":143},{\"lon\":30.47547,\"lat\":50.41097,\"ele\":143},{\"lon\":30.47559,\"lat\":50.41099,\"ele\":142},{\"lon\":30.47567,\"lat\":50.41103,\"ele\":141},{\"lon\":30.47574,\"lat\":50.41111,\"ele\":140},{\"lon\":30.47608,\"lat\":50.41108,\"ele\":139},{\"lon\":30.47673,\"lat\":50.41086,\"ele\":137},{\"lon\":30.47691,\"lat\":50.4107,\"ele\":137},{\"lon\":30.4776,\"lat\":50.41015,\"ele\":134},{\"lon\":30.47774,\"lat\":50.41008,\"ele\":134},{\"lon\":30.47788,\"lat\":50.41011,\"ele\":134},{\"lon\":30.47825,\"lat\":50.41032,\"ele\":136},{\"lon\":30.47855,\"lat\":50.41032,\"ele\":137},{\"lon\":30.47928,\"lat\":50.40958,\"ele\":135},{\"lon\":30.4801,\"lat\":50.40854,\"ele\":133},{\"lon\":30.4814,\"lat\":50.40811,\"ele\":135},{\"lon\":30.48206,\"lat\":50.40782,\"ele\":132},{\"lon\":30.48235,\"lat\":50.4076,\"ele\":130},{\"lon\":30.48256,\"lat\":50.40757,\"ele\":130},{\"lon\":30.48307,\"lat\":50.40726,\"ele\":131},{\"lon\":30.48311,\"lat\":50.40721,\"ele\":132},{\"lon\":30.48341,\"lat\":50.40731,\"ele\":131},{\"lon\":30.48507,\"lat\":50.40736,\"ele\":131},{\"lon\":30.48514,\"lat\":50.40713,\"ele\":133},{\"lon\":30.48546,\"lat\":50.40654,\"ele\":139},{\"lon\":30.4855,\"lat\":50.40642,\"ele\":140},{\"lon\":30.48536,\"lat\":50.40567,\"ele\":146},{\"lon\":30.4854,\"lat\":50.40539,\"ele\":147},{\"lon\":30.48558,\"lat\":50.40506,\"ele\":147},{\"lon\":30.48564,\"lat\":50.405,\"ele\":147},{\"lon\":30.48574,\"lat\":50.40495,\"ele\":147},{\"lon\":30.4862,\"lat\":50.40479,\"ele\":147},{\"lon\":30.48669,\"lat\":50.40474,\"ele\":145},{\"lon\":30.48653,\"lat\":50.40434,\"ele\":148},{\"lon\":30.48644,\"lat\":50.40403,\"ele\":150},{\"lon\":30.48573,\"lat\":50.40359,\"ele\":153},{\"lon\":30.48514,\"lat\":50.40326,\"ele\":156},{\"lon\":30.48472,\"lat\":50.40289,\"ele\":159},{\"lon\":30.48449,\"lat\":50.40263,\"ele\":161},{\"lon\":30.48488,\"lat\":50.40257,\"ele\":160},{\"lon\":30.48532,\"lat\":50.40258,\"ele\":159},{\"lon\":30.48538,\"lat\":50.40246,\"ele\":159},{\"lon\":30.48526,\"lat\":50.40226,\"ele\":160},{\"lon\":30.48515,\"lat\":50.40199,\"ele\":161},{\"lon\":30.48514,\"lat\":50.40164,\"ele\":161},{\"lon\":30.48539,\"lat\":50.40139,\"ele\":158},{\"lon\":30.48555,\"lat\":50.40128,\"ele\":156},{\"lon\":30.48564,\"lat\":50.40119,\"ele\":154},{\"lon\":30.48562,\"lat\":50.40106,\"ele\":153},{\"lon\":30.4854,\"lat\":50.40072,\"ele\":150},{\"lon\":30.48507,\"lat\":50.40037,\"ele\":148},{\"lon\":30.48502,\"lat\":50.40025,\"ele\":147},{\"lon\":30.48505,\"lat\":50.40009,\"ele\":146},{\"lon\":30.48526,\"lat\":50.39991,\"ele\":144},{\"lon\":30.4865,\"lat\":50.39959,\"ele\":144},{\"lon\":30.48671,\"lat\":50.39973,\"ele\":145},{\"lon\":30.48763,\"lat\":50.39936,\"ele\":152},{\"lon\":30.48798,\"lat\":50.39906,\"ele\":157},{\"lon\":30.48826,\"lat\":50.39872,\"ele\":162},{\"lon\":30.48908,\"lat\":50.39762,\"ele\":176},{\"lon\":30.48919,\"lat\":50.39739,\"ele\":178},{\"lon\":30.48944,\"lat\":50.39661,\"ele\":182},{\"lon\":30.48965,\"lat\":50.39607,\"ele\":182},{\"lon\":30.49009,\"lat\":50.39473,\"ele\":185},{\"lon\":30.4905,\"lat\":50.39367,\"ele\":186},{\"lon\":30.49168,\"lat\":50.39386,\"ele\":185},{\"lon\":30.49262,\"lat\":50.39156,\"ele\":183},{\"lon\":30.49334,\"lat\":50.38971,\"ele\":168},{\"lon\":30.49362,\"lat\":50.38941,\"ele\":164},{\"lon\":30.49448,\"lat\":50.38855,\"ele\":159}]]}}";
    static String TRACK;
    static ObjectMapper mapper = new ObjectMapper();
    public Mytrack(String path){
        LoadFile loadFile= new LoadFile(path);
        TRACK = loadFile.getText();
    }
    public LatLng[] getTrack(){
        LatLng[] latLngs = new LatLng[0];
        return pareGpx();
    }

    public LatLng[] pareGpx() {
        LatLng[] latLngs = new LatLng[0];
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(TRACK));
            Document doc = db.parse(is);
            NodeList trkseg = doc.getElementsByTagName("trkseg");
            int i = 0, k =0, count=0;
            for (i = 0; i < trkseg.getLength(); i++){
                Element element = (Element) trkseg.item(i);
                NodeList trkpt = element.getElementsByTagName("trkpt");
                for(k = 0; k<trkpt.getLength(); k++){
                   count++;
                }
            }
            latLngs = new LatLng[count];
            count=0;
            for (i = 0; i < trkseg.getLength(); i++) {
                Element element = (Element) trkseg.item(i);
                NodeList trkpt = element.getElementsByTagName("trkpt");
                for(k = 0; k<trkpt.getLength(); k++){
                    Element ff = (Element) trkpt.item(k);
                    String lat =  ff.getAttribute("lat");
                    String lng =  ff.getAttribute("lon");
                    latLngs[count] = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    count ++;
                    Log.d("myLog", lat+" : " + lng);
                }
            }

        }catch (Exception e){
            Log.e("myLog", "pareGpx +++ " + e.toString(), e);
        }




        return latLngs;
    }

    public LatLng[] _getTrack() {
        LatLng[] latLngs = new LatLng[0];
        ObjectNode root = null;
        try {
            if(TRACK!=null)
            root = (ObjectNode) mapper.readTree(TRACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) {
            ObjectNode data = (ObjectNode) root.get("data");
            ArrayNode trackData = (ArrayNode) data.get("trackData");
            ArrayNode points = (ArrayNode) trackData.get(0);
            int lenght = points.size();
            latLngs = new LatLng[lenght];
            int i = 0;
            for (JsonNode point : points) {
                latLngs[i] = new LatLng(point.path("lat").asDouble(), point.path("lon").asDouble());
                i++;
            }

        }

        return latLngs;

    }

    ;

}
