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

    public ChessPosition(int row, int col) {
        this.row = row - 1;
        this.col = col - 1;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row + 1;
    }

    @Override
    public int hashCode() {
        return (row + 1) + (col + 1) * 10;
    }

    @Override
    public boolean equals(Object obj) {
        ChessPosition other = (ChessPosition) obj;
        return this.getRow() == other.getRow() && this.getColumn() == other.getColumn();
    }

    @Override
    public String toString() {
        return "{" + getRow() + "," + getColumn() +"}";
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col + 1;
    }
}
