package game.auxilary;

import game.Board;
import game.Cell;
import game.IndexVault;
import java.util.ArrayList;

public class AuxilaryMethodsPlace {
    public static boolean checkVerticalShipPlaceConstraint(int rowCounter, int columnCounter, Integer predI, Integer predJ) {
        if (predI == null) {
            predI = rowCounter;
        }

        if (predJ == null) {
            predJ = columnCounter;
        }

        if (rowCounter == predI - 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI && columnCounter == predJ + 1) {
            return false;
        }

        predI = rowCounter;
        predJ = columnCounter;
        return true;
    }

    public static boolean checkHorizontalShipPlaceConstraint(int rowCounter, int columnCounter, Integer predI, Integer predJ) {
        if (predI == null) {
            predI = rowCounter;
        }

        if (predJ == null) {
            predJ = columnCounter;
        }

        if (rowCounter == predI - 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ - 1) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ + 1) {
            return false;
        } else if (rowCounter == predI - 1 && columnCounter == predJ) {
            return false;
        } else if (rowCounter == predI + 1 && columnCounter == predJ) {
            return false;
        }

        predI = rowCounter;
        predJ = columnCounter;
        return true;
    }

    public static boolean hasShipsNear(int rowIndex, int columnIndex, Board yourBoard) {
        int counter = 0;
        Cell[][] cells = yourBoard.getCells();
        int imax,imin,jmax,jmin;

        if (rowIndex == 0) {
            imin = rowIndex;
        } else {
            imin = rowIndex - 1;
        }
        if (rowIndex == 9) {
            imax = rowIndex;
        } else {
            imax = rowIndex + 1;
        }
        if (columnIndex == 0) {
            jmin = columnIndex;
        } else {
            jmin = columnIndex - 1;
        }
        if (columnIndex == 9) {
            jmax = columnIndex;
        } else {
            jmax = columnIndex + 1;
        }

        for (int i = imin; i <= imax; i++) {
            for (int j = jmin; j <= jmax; j++) {
                if (cells[i][j].isWithShip() == true) {
                    counter++;
                }
            }
        }

        if (counter > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void freezeCell(ArrayList<IndexVault> itShouldFrozen, Board yourBoard) {
        IndexVault vaultFirst = itShouldFrozen.get(0);
        IndexVault vaultLast = itShouldFrozen.get(itShouldFrozen.size() - 1);
        IndexVault first = IndexVault.getHigherVault(vaultFirst, vaultLast);
        IndexVault last = IndexVault.getLowerVault(vaultFirst, vaultLast);

        int imin = first.getI();
        int imax = last.getI();
        int jmin = first.getJ();
        int jmax = last.getJ();

        if (imin != 0) {
            imin -= 1;
        }

        if (imax != 9) {
            imax += 1;
        }

        if (jmin != 0) {
            jmin -= 1;
        }

        if (jmax != 9) {
            jmax += 1;
        }

        for (int i = imin; i <= imax; i++) {
            for (int j = jmin; j <= jmax; j++) {
                yourBoard.getIndexCell(i, j).setFrozen(true);
            }
        }
    }
}
