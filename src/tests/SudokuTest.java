package tests;

import org.junit.Test;
import org.testng.Assert;
import sudoku.ParseException;
import sudoku.Sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SudokuTest {

	private static final String DIR_SAMPLES = "samples/";

	/*
    * make sure assertions are turned on!
    * we don't want to run sudoku.test.test cases without assertions too.
    * see the handout to find out how to turn them on.
    */
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test(expected = IllegalArgumentException.class)
	public void negativeBlockSize () {
		new Sudoku(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongRowSize () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size - 1];
		}

		new Sudoku(blockSize, cells);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongColumnSize () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size - 1][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size - 1];
		}

		new Sudoku(blockSize, cells);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidCellValues () {
		final int blockSize = 3;
		final int size = (int) Math.pow(blockSize, 2);
		final int[][] cells = new int[size][];

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new int[size];
		}

		cells[4][0] = -3;
		cells[0][4] = 12;

		new Sudoku(blockSize, cells);
	}

	/**
	 * Tests the isValid() method of Sudoku by feeding supposedly valid Sudoku grids.
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testIsValid () throws IOException, ParseException {
    	final String dir = "samples/";
    	String[] files = {
				"sudoku_easy.txt",
				"sudoku_hard.txt",
				"sudoku_hard4.txt"
    	};

    	for (String file: files) {
    		Assert.assertTrue(
    				Sudoku.fromFile(3, dir + file).isValid(),
					String.format("\"%s\" is an invalid valid Sudoku grid, or other errors were encountered.\n", file)
			);
		}
	}

	/**
	 * Tests the isValid() method of Sudoku by feeding invalid Sudoku grids.
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testIsValidNegated () throws IOException, ParseException {
    	final String dir = "samples/";
    	String[] files = {
				"sudoku_wrong_easy.txt",
				"sudoku_wrong_hard2.txt",
				"sudoku_wrong_evil.txt"
    	};

    	for (String file: files) {
    		Assert.assertTrue(
    				Sudoku.fromFile(3, dir + file).isValid() == false,
					String.format("\"%s\" is a valid Sudoku grid, or other errors were encountered.\n", file)
			);
		}
	}

	@Test
	public void testToString () {
		final int[][] cells = {
				{1, 4, 3, 0},
				{3, 0, 4, 1},
				{0, 1, 4, 3},
				{0, 3, 0, 2}
		};
		final String expectedResult =
						"143.\n" +
						"3.41\n" +
						".143\n" +
						".3.2\n";
		final Sudoku s = new Sudoku(2, cells);

		Assert.assertEquals(
				s.toString(),
				expectedResult
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFileInvalidBlockSize () throws IOException, ParseException {
		final String fileName = "samples/sudoku_hard4.txt";
		Sudoku.fromFile(Sudoku.BLOCK_SIZE_MAX + 1, fileName);
	}

	@Test
	public void testFromFiles () throws IOException, ParseException {
		final File samplesDir = new File(DIR_SAMPLES);
		final File[] sampleFiles;
		final Set<String> excludedFiles = new TreeSet<>();
		final FilenameFilter filter = (dir, name) -> !excludedFiles.contains(name);
		Sudoku s;

		excludedFiles.add("sudoku_4x4.txt");
		excludedFiles.add("README");

		if (samplesDir.isDirectory()) {
			sampleFiles = samplesDir.listFiles(filter);

			if (sampleFiles != null) {
				for (File f: sampleFiles) {
					s = Sudoku.fromFile(3, f.getPath());

					Assert.assertEquals(
							s.toString(),
							readFile(f)
					);
				}
			}
		}
	}

	@Test
	public void testGetCellByBlock () throws IOException, ParseException {
    	final int blockSize = 3, blockSizePow = (int) Math.pow(blockSize, 2);
    	final Sudoku expected = Sudoku.fromFile(blockSize, "samples/sudoku_hard4.txt");
    	final Sudoku built;

    	final int[][] builtSquares = new int[blockSizePow][blockSizePow];
    	Sudoku.SudokuCell tempCell;

		for (int b = 0; b < blockSizePow; b++) {
			for (int c = 0; c < blockSizePow; c++) {
				tempCell = expected.getCellByBlock(b, c);
				builtSquares[tempCell.row][tempCell.column] = tempCell.value;
			}
		}

		built = new Sudoku(blockSize, builtSquares);

		Assert.assertEquals(
				expected,
				built
		);
	}

	private String readFile (File file) throws FileNotFoundException {
		Scanner in = null;
		final StringBuilder b = new StringBuilder();

		try {
			in = new Scanner(file);

			while (in.hasNextLine()) {
				b.append(in.nextLine()).append("\n");
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return b.toString();
	}

}