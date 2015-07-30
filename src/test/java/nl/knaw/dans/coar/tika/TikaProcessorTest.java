package nl.knaw.dans.coar.tika;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import nl.knaw.dans.coar.walk.ArchisDetector;
import nl.knaw.dans.coar.walk.CoordinateDetector;

import org.apache.tika.exception.TikaException;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TikaProcessorTest
{
    
    private TikaProcessor processor = new TikaProcessor();
    
    private Object[][] testdata = {
            {"12101870 OSS.AGR.ARC.RAP (Vlierbosstraat 1 Ravestein).pdf", 1, 6}, // 1 false positive
            {"366_WAR_71.pdf", 10, 3}, //
            {"ADC rapport 1.pdf", 1, 1}, // 
            {"Bijlage 2 - boorformulieren verkennend onderzoek ABC-Terrein Opheusden.pdf", 34, 0}, //
            {"Delfzijl Singel 52 en Schoolstraat 27 (gemeente Delfzijl) Libau 09-124.pdf", 2, 2}, //
            {"Rapport-definitief_Druten, Laarstraat_28425_v1.1.pdf", 11, 4}, //
            {"STAR 73 tekst.pdf", 0, 0}, // ESP Ghostscript 7.07. tika only sees diamond ?
            {"ADC 117.pdf", 1, 1}, //
            {"Rap 184.pdf", 14, 10}, //
            {"RAP 479_Venlo Blerick Rutgerusgang.pdf", 5, 3}, // 1 false positive
            {"RAAP rapport 422.pdf", 0, 0}, //
            {"RAAPRAPPORT 785.pdf", 18, 4}, //
            {"NO1151-BNBK.pdf", 1, 0}, //
            {"NO1208-BOWZ.pdf", 1, 0}, //
            {"NO1208-BOWZ_boorbeschrijving.pdf", 7, 0}, //
            {"NO1388-MAWO.pdf", 9, 6}, //
            {"RAAP-NOTITIE 226.pdf", 1, 1}, //
            {"RAAP-NOTITIE 568.pdf", 2, 8}, //
            {"RAAP-NOTITIE-932.pdf", 1, 0}, //
            {"2003-07_9.pdf", 1, 0}, //
            {"2005-09_4.pdf", 3, 2}, // more than 3
            {"06.047.pdf", 4, 4}, //
            {"Bilan 2003-3.pdf", 3, 2}, // 1 in kaart
            {"RAP 973_4107256_Leerdam oosterwijk Lingedijk.pdf", 6, 3}, // 1 false pos.
            {"Steekproef 2002 09-01.pdf", 0, 1}, //
            {"AM12356.def.pdf", 5, 3}, //
            {"Oranjewoud 2002_02 Appingedam, Jukwerd.pdf", 0, 0}, // tika leeg
            {"34907 Almelo Thorbeckelaan BO en IVO.pdf", 10, 10}, //
            {"20141112-267946 rap BO Bûtefjild rev 0A incl. bijlagen.pdf", 7, 2}, //
            {"arcrapport-2003-1.pdf", 0, 0}, // tikfout in coordinaten
            {"arcrapport-2004-43 Noordoostpolder, Luttelgeest.pdf", 6, 2}, //
            {"arcpublicatie-71.pdf", 1, 0}, //
            {"Archeologisch bureauonderzoek bergbezinkbasin ArminiuspleinWestsingel Oudewater.pdf", 1, 2}, //
            {"110315NA8013.pdf", 4, 5}, //
            {"ArchaeoBone 3 Tabellen.pdf", 0, 0}, //
            {"24241_definitief_eindrapport_Bergharen 4 locaties.pdf", 20, 2}, //
            {"archol_39.pdf", 1, 1}, //
            {"Muiden Muiderberg badlaan 3 BURO.pdf", 4, 0}, //
            {"Heerhugowaard_Groenedijk_1_BB.pdf", 4, 2}, //
            {"01.082.pdf", 2, 0}, //
            {"07.0469.pdf", 96, 3}, //
            {"V-15.0047-Veghel_Burgemeester de Kuiperlaan 10-definitief(1).pdf", 18, 3}, //
            {"08025211 BRO.BUU.ARC Eindrapportage archeologisch bureauonderzoek Boldijk 5 5a en 6 te Halle.pdf", 8, 5}, //
            {"ara011_eindrapport stichtse brug_waardering+ro.pdf", 4, 0}, //
            {"Leusden, Nieuw Princenhof.pdf", 5, 0}, //
            {"AOO7-LVK2a.pdf", 1, 4}, //
            {"55 Bureauonderzoek Rijksweg en Burg Dorth tot Medlerstraat Duiven.pdf", 4, 8}
            
    };
    
    
    @Test
    public void testDetectors() throws Exception {
        processor.addBodyHandler(new CoordinateDetector());
        processor.addBodyHandler(new ArchisDetector());
        
        for (Object[] data : testdata) {
            String pdf = (String) data[0];
            int spatialCount = (int) data[1];
            int numberCount = (int) data[2];
            detect(pdf, spatialCount, numberCount, false, false);
        }
    }

    protected void detect(String pdf, int spatialCount, int numberCount, boolean printSpatials, boolean printNumbers) throws FileNotFoundException, IOException, SAXException, TikaException
    {
        TikaProfile profile = new TikaProfile();
        profile.setDsLabel(pdf);
        File pdfFile = new File("non-pub/test-files/pdf/" + pdf);
        InputStream ins = new FileInputStream(pdfFile);

        processor.process(profile, ins);
        
        ins.close();
        
        int sp = profile.getSpatials().size();
        
        if (sp != spatialCount || printSpatials) {
            System.err.println("\n");
            System.err.println(pdf + "\nExpected " + spatialCount + " spatials, detected " + sp);
            for (Spatial spatial : new TreeSet<Spatial>(profile.getSpatials())) {
                System.err.println(spatial);
            }
        }
        
        int an = profile.getArchisNummers().size();
        
        if (an != numberCount || printNumbers) {
            System.err.println("\n");
            System.err.println(pdf + "\nExpected " + numberCount + " numbers, detected " + an);
            for (ArchisNummer anummer : new TreeSet<ArchisNummer>(profile.getArchisNummers())) {
                System.err.println(anummer);
            }
        }
        assertEquals(spatialCount, sp);
        assertEquals(numberCount, an);
    }

}
