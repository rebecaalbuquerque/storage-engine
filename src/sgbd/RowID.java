package sgbd;

import static utils.ConversorUtils.*;

public class RowID {

    private byte idFile;
    private byte[] idBloco = new byte[3];
    private byte[] idTupla = new byte[2];

    public RowID(int idFile, int idBloco, int idTupla){
        setIdFile(intToArrayByte(idFile, 1)[0]);
        setIdBloco(intToArrayByte(idBloco, 3));
        setIdTupla(intToArrayByte(idTupla, 2));
    }

    public RowID(String rowId){
        String[] idArray = rowId.split("-");

        setIdFile(intToArrayByte(Integer.parseInt(idArray[0]), 1)[0]);
        setIdBloco(intToArrayByte(Integer.parseInt(idArray[1]), 3));

        if(idArray.length > 2){
            setIdTupla(intToArrayByte(Integer.parseInt(idArray[2]), 2));
        }
    }

    public byte[] getIdTupla() {
        return idTupla;
    }

    public void setIdTupla(byte[] idTupla) {
        this.idTupla = idTupla;
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
