package sgbd;

import static utils.ConversorUtils.*;

public class PageID {

    private byte idFile;
    private byte[] idBloco = new byte[3];

    public PageID(int idFile, int idBloco){
        setIdFile(intToArrayByte(idFile, 1)[0]);
        setIdBloco(intToArrayByte(idBloco, 3));
    }

    public PageID(String rowId){
        String[] idArray = rowId.split("-");

        setIdFile(intToArrayByte(Integer.parseInt(idArray[0]), 1)[0]);
        setIdBloco(intToArrayByte(Integer.parseInt(idArray[1]), 3));
    }

    public int getIdBlocoAsInt(){
        return getIntFrom3Bytes(idBloco);
    }

    public int getIdFileAsInt(){
        return (int) idFile;
    }

    public byte getIdFile() {
        return idFile;
    }

    private void setIdFile(byte idFile) {
        this.idFile = idFile;
    }

    public byte[] getIdBloco() {
        return idBloco;
    }

    private void setIdBloco(byte[] idBloco) {
        this.idBloco = idBloco;
    }
}
