package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 1; i <= row; i++) {
            for (int j = 1; i <= col; i ++) {
                hash += i * j;
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public ChessPosition(int row, int col) {
        this.row = row - 1;
        this.col = col - 1;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row + 1;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col + 1;
    }

    @Override
    public String toString() {
        return "{" + (this.row + 1) +", " + (this.col + 1) + "}";
    }
}
