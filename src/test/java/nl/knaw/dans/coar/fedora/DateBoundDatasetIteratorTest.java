package nl.knaw.dans.coar.fedora;

import org.junit.Test;

public class DateBoundDatasetIteratorTest extends AbstractFedoraTest
{
    
    @Test
    public void getSome() throws Exception {
        int max = 200;
        DateBoundDatasetIterator iter = new DateBoundDatasetIterator();
        while (iter.hasNext() && iter.getCount() <= max) {
            String datasetId = iter.next();
            System.out.println(datasetId + " " + iter.getDatePointer());
        }
    }
    
    @Test
    public void doesitwork() throws Exception {
        DatasetIterator iter = new DatasetIterator() {
            
            protected String getQuery() {
                return "pid%7Eeasy-dataset:* mDate%3C2015-10-28";
            };
        };
        while (iter.hasNext()) {
            System.err.println(iter.next());
        }
    }

}
