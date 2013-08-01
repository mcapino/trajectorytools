package tt.parallel;

import tt.util.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ParametrIterator implements Iterator<String[]> {

    private BufferedReader reader;
    private String separator;

    public ParametrIterator(Reader reader, String separator) {
        this.reader = new BufferedReader(reader);
        this.separator = separator;
    }


    @Override
    public boolean hasNext() {
        try {
            return reader.ready();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String[] next() {
        try {
            return reader.readLine().trim().split(separator);
        } catch (IOException e) {
            throw new NoSuchElementException("BufferedReader has thrown a IOException");
        }
    }

    @Override
    public void remove() {
        throw new NotImplementedException("This class is for reading only");
    }
}
