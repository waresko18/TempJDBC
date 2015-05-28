package cz.vutbr.fit.jdbc.temp.difference;

/**
 * Podporovane casove intervaly
 * @author Filip Fekiac
 */
public enum DateTimeScale
{
  UNDEFINED(0L),  SECOND(1L),  MINUTE(60L),  HOUR(3600L),  DAY(86400L),  MONTH(2628000L),  YEAR(31536000L);
  
  private final long chronons;
  
  private DateTimeScale(long chronons)
  {
    this.chronons = chronons;
  }
  
  public long getChronons()
  {
    return this.chronons;
  }
}