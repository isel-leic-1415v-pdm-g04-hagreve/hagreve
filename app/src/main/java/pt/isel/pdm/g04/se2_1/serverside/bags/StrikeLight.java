package pt.isel.pdm.g04.se2_1.serverside.bags;

import java.util.Date;

public class StrikeLight {
    public int id;
    public Company company; // Descreve a empresa afectada pela greve.
    public String description; // Uma descrição da greve ("This is a strike description.");
    public Date start_date, end_date; // Datas para o início e fim da greve ("2011-11-30 20:25:23");
    public String source_link; // URL da fonte dos dados ("http://example.com");
    public boolean all_day; // true se a greve for de dias inteiros (true)
    public boolean canceled; // true se a greve tiver sido cancelada (false);
    public Submitter submitter; // Descreve o autor do aviso.
}
