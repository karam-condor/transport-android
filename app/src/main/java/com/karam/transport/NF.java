package com.karam.transport;

public class NF {
    Long numnota, numcar, codcli, codusur,pendcodprocess,numped,numtransvenda;
    String cliente, email_cliente,email_cliene2, uf, ciddade, bairro, obs1, obs2, obs3, obsentrega, rca, email_rca, dtent,penddtent,endereco,cep,pendobs;
    Float latent, longtent, pendlat, pendlongt;
    Integer stenvi, stent, stpend,stcred,codmotivo;


    public Long getNumped() {
        return numped;
    }

    public void setNumped(Long numped) {
        this.numped = numped;
    }

    public Long getNumtransvenda() {
        return numtransvenda;
    }

    public void setNumtransvenda(Long numtransvenda) {
        this.numtransvenda = numtransvenda;
    }

    public Integer getCodmotivo() {
        return codmotivo;
    }

    public void setCodmotivo(Integer codmotivo) {
        this.codmotivo = codmotivo;
    }

    public Long getPendcodprocess() {
        return pendcodprocess;
    }

    public void setPendcodprocess(Long pendcodprocess) {
        this.pendcodprocess = pendcodprocess;
    }

    public String getPenddtent() {
        return penddtent;
    }

    public void setPenddtent(String penddtent) {
        this.penddtent = penddtent;
    }

    public String getPendobs() {
        return pendobs;
    }

    public void setPendobs(String pendobs) {
        this.pendobs = pendobs;
    }

    public NF(){}

    public NF(Long numnota, Long numcar, Long codcli, Long codusur,
              String cliente, String email_cliente,String email_cliene2, String uf, String ciddade,
              String bairro, String obs1, String obs2, String obs3, String obsentrega,
              String rca, String email_rca, String dtent, Float latent, Float longtent,
              Float pendlat, Float pendlongt, Integer stenvi, Integer stent, Integer stpend,Integer stcred,String endereco,String cep,long numped,long numtransvenda) {
        this.numped = numped;
        this.numtransvenda = numtransvenda;
        this.numnota = numnota;
        this.numcar = numcar;
        this.codcli = codcli;
        this.codusur = codusur;
        this.cliente = cliente;
        this.email_cliente = email_cliente;
        this.email_cliene2 = email_cliene2;
        this.uf = uf;
        this.ciddade = ciddade;
        this.bairro = bairro;
        this.obs1 = obs1;
        this.obs2 = obs2;
        this.obs3 = obs3;
        this.obsentrega = obsentrega;
        this.rca = rca;
        this.email_rca = email_rca;
        this.dtent = dtent;
        this.latent = latent;
        this.longtent = longtent;
        this.pendlat = pendlat;
        this.pendlongt = pendlongt;
        this.stenvi = stenvi;
        this.stent = stent;
        this.stpend = stpend;
        this.stcred = stcred;
        this.endereco = endereco;
        this.cep = cep;
    }

    public NF(Long numnota, Long numcar, Long codcli, Long codusur,
              String cliente, String email_cliente,String email_cliene2, String uf, String ciddade,
              String bairro, String obs1, String obs2, String obs3, String obsentrega,
              String rca, String email_rca, String dtent, Float latent, Float longtent,
              Float pendlat, Float pendlongt, Integer stenvi, Integer stent, Integer stpend,
              Integer stcred,String endereco,String cep,Long pendcodprocess,
              String penddtent,String pendobs,long numped,long numtransvenda) {
        this.numped = numped;
        this.numtransvenda = numtransvenda;
        this.numnota = numnota;
        this.numcar = numcar;
        this.codcli = codcli;
        this.codusur = codusur;
        this.cliente = cliente;
        this.email_cliente = email_cliente;
        this.email_cliene2 = email_cliene2;
        this.uf = uf;
        this.ciddade = ciddade;
        this.bairro = bairro;
        this.obs1 = obs1;
        this.obs2 = obs2;
        this.obs3 = obs3;
        this.obsentrega = obsentrega;
        this.rca = rca;
        this.email_rca = email_rca;
        this.dtent = dtent;
        this.latent = latent;
        this.longtent = longtent;
        this.pendlat = pendlat;
        this.pendlongt = pendlongt;
        this.stenvi = stenvi;
        this.stent = stent;
        this.stpend = stpend;
        this.stcred = stcred;
        this.endereco = endereco;
        this.cep = cep;
        this.pendcodprocess = pendcodprocess;
        this.pendobs = pendobs;
        this.penddtent = penddtent;
    }




    public Integer getStcred() {
        return stcred;
    }

    public void setStcred(Integer stcred) {
        this.stcred = stcred;
    }

    public String getEmail_cliene2() {
        return email_cliene2;
    }

    public void setEmail_cliene2(String email_cliene2) {
        this.email_cliene2 = email_cliene2;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getNumnota() {
        return numnota;
    }

    public void setNumnota(Long numnota) {
        this.numnota = numnota;
    }

    public Long getNumcar() {
        return numcar;
    }

    public void setNumcar(Long numcar) {
        this.numcar = numcar;
    }

    public Long getCodcli() {
        return codcli;
    }

    public void setCodcli(Long codcli) {
        this.codcli = codcli;
    }

    public Long getCodusur() {
        return codusur;
    }

    public void setCodusur(Long codusur) {
        this.codusur = codusur;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEmail_cliente() {
        return email_cliente;
    }

    public void setEmail_cliente(String email_cliente) {
        this.email_cliente = email_cliente;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCiddade() {
        return ciddade;
    }

    public void setCiddade(String ciddade) {
        this.ciddade = ciddade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getObs1() {
        return obs1;
    }

    public void setObs1(String obs1) {
        this.obs1 = obs1;
    }

    public String getObs2() {
        return obs2;
    }

    public void setObs2(String obs2) {
        this.obs2 = obs2;
    }

    public String getObs3() {
        return obs3;
    }

    public void setObs3(String obs3) {
        this.obs3 = obs3;
    }

    public String getObsentrega() {
        return obsentrega;
    }

    public void setObsentrega(String obsentrega) {
        this.obsentrega = obsentrega;
    }

    public String getRca() {
        return rca;
    }

    public void setRca(String rca) {
        this.rca = rca;
    }

    public String getEmail_rca() {
        return email_rca;
    }

    public void setEmail_rca(String email_rca) {
        this.email_rca = email_rca;
    }

    public String getDtent() {
        return dtent;
    }

    public void setDtent(String dtent) {
        this.dtent = dtent;
    }

    public Float getLatent() {
        return latent;
    }

    public void setLatent(Float latent) {
        this.latent = latent;
    }

    public Float getLongtent() {
        return longtent;
    }

    public void setLongtent(Float longtent) {
        this.longtent = longtent;
    }


    public Float getPendlat() {
        return pendlat;
    }

    public void setPendlat(Float pendlat) {
        this.pendlat = pendlat;
    }

    public Float getPendlongt() {
        return pendlongt;
    }

    public void setPendlongt(Float pendlongt) {
        this.pendlongt = pendlongt;
    }

    public Integer getStenvi() {
        return stenvi;
    }

    public void setStenvi(Integer stenvi) {
        this.stenvi = stenvi;
    }

    public Integer getStent() {
        return stent;
    }

    public void setStent(Integer stent) {
        this.stent = stent;
    }

    public Integer getStpend() {
        return stpend;
    }

    public void setStpend(Integer stpend) {
        this.stpend = stpend;
    }
}