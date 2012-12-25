/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.ais.message;

import dk.dma.ais.binary.BinArray;
import dk.dma.ais.binary.SixbitEncoder;
import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.sentence.Vdm;

/**
 * AIS message 19
 * 
 * Extended Class B equipment position report as defined by ITU-R M.1371-4
 * 
 */
public class AisMessage19 extends AisMessage {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Reserved for definition by a competent regional or local authority.
	 * Should be set to zero, if not used for any regional or local application.
	 * Regional applications should not use zero.
	 */
	private int spare1 = 0; // 8 bits

	/**
	 * Speed over ground in 1/10 knot steps (0-102.2 knots) 1023 = not
	 * available, 1022 = 102.2 knots or higher
	 */
	private int sog; // 10 bits

	/**
	 * AisPosition Accuracy 1 = high ( =< 10 m) 0 = low (>10 m) 0 = default The
	 * PA flag should be determined in accordance with Table 47
	 */
	private int posAcc; // 1 bit

	/**
	 * Store the positions just as in message 1-3
	 */
	private AisPosition pos; // : Lat/Long 1/10000 minute

	/**
	 * Course over ground in 1/10 = (0-3599). 3600 (E10h) = not available =
	 * default; 3601-4095 should not be used
	 */
	private int cog; // 12 bits

	/**
	 * True heading Degrees (0-359) (511 indicates not available = default)
	 */
	private int trueHeading; // 9 bits

	/**
	 * Time stamp: UTC second when the report was generated by the EPFS (0-59 or
	 * 60 if time stamp is not available, which should also be the default value
	 * or 61 if positioning system is in manual input mode or 62 if electronic
	 * position fixing system operates in estimated (dead reckoning) mode or 63
	 * if the positioning system is inoperative) 61, 62, 63 are not used by CS
	 * AIS
	 */
	private int utcSec; // 6 bits : UTC Seconds

	/**
	 * Not used. Should be set to zero. Reserved for future use
	 */
	private int spare2 = 0; // 4 bits

	/**
	 * Ship name: Maximum 20 characters 6 bit ASCII, as defined in Table 44
	 * 
	 * @@@@@@@@@@@@@@@@@@@@ = not available = default. For SAR aircraft, it
	 *                      should be set to "SAR AIRCRAFT NNNNNNN" where
	 *                      NNNNNNN equals the aircraft registration number
	 */
	private String name; // 20x6 (120) bits

	/**
	 * Type of ship and cargo type: 0 = not available or no ship = default 1-99
	 * = as defined in § 3.3.2 100-199 = reserved, for regional use 200-255 =
	 * reserved, for future use Not applicable to SAR aircraft
	 */
	private int shipType; // 8 bits

	/**
	 * GPS Ant. Distance from bow (A): Reference point for reported position.
	 * Also indicates the dimension of ship (m) (see Fig. 42 and § 3.3.3)
	 * 
	 * NOTE: When GPS position is not available, but the ships dimensions is
	 * available, then this field should be 0
	 */
	private int dimBow; // 9 bits

	/**
	 * GPS Ant. Distance from stern (B) Reference point for reported position.
	 * Also indicates the dimension of ship (m) (see Fig. 42 and § 3.3.3)
	 * 
	 * NOTE: When GPS position is not available, but the ships dimensions is
	 * available, then this field should be representing the length of the ship
	 */
	private int dimStern; // 9 bits

	/**
	 * GPS Ant. Distance from port (C) Reference point for reported position.
	 * Also indicates the dimension of ship (m) (see Fig. 42 and § 3.3.3)
	 * 
	 * NOTE: When GPS position is not available, but the ships dimensions is
	 * available, then this field should be 0
	 */
	private int dimPort; // 6 bits

	/**
	 * GPS Ant. Distance from starboard (D): Reference point for reported
	 * position. Also indicates the dimension of ship (m) (see Fig. 42 and §
	 * 3.3.3)
	 * 
	 * NOTE: When GPS position is not available, but the ships dimensions is
	 * available, then this field should be representing the with of the ship
	 */
	private int dimStarboard; // 6 bits

	/**
	 * Type of electronic position fixing device: 0 = undefined (default) 1 =
	 * GPS 2 = GLONASS 3 = combined GPS/GLONASS 4 = Loran-C 5 = Chayka 6 =
	 * integrated navigation system 7 = surveyed 8 = Galileo, 9-14 = not used 15
	 * = internal GNSS
	 */
	private int posType; // 4 bits

	/**
	 * RAIM-flag: RAIM (Receiver autonomous integrity monitoring) flag of
	 * electronic position fixing device; 0 = RAIM not in use = default; 1 =
	 * RAIM in use see Table 47
	 */
	private int raimFlag; // 1 bit

	/**
	 * DTE: Data terminal equipment (DTE) ready 0 = available 1 = not available
	 * = default see § 3.3.1
	 */
	private int dte; // 1 bit : DTE flag

	/**
	 * Mode flag: 0 = Station operating in autonomous and continuous mode =
	 * default 1 = Station operating in assigned mode
	 */
	private int modeFlag; // 1 bit

	/**
	 * Not used. Should be set to zero. Reserved for future use
	 */
	private int spare3 = 0; // 4 bits

	public AisMessage19(Vdm vdm) throws AisMessageException, SixbitException {
		super(vdm);
		parse(vdm.getBinArray());
	}

	@Override
	protected void parse(BinArray binArray) throws AisMessageException,
			SixbitException {
		BinArray sixbit = vdm.getBinArray();
        if (sixbit.getLength() != 312) {
            throw new AisMessageException("Message 19 wrong length " + sixbit.getLength());
        }

        super.parse(sixbit);

        this.spare1 = (int) sixbit.getVal(8);
        this.sog = (int) sixbit.getVal(10);
        this.posAcc = (int) sixbit.getVal(1);

        this.pos = new AisPosition();
        this.pos.setRawLongitude(sixbit.getVal(28));
        this.pos.setRawLatitude(sixbit.getVal(27));

        this.cog = (int) sixbit.getVal(12);
        this.trueHeading = (int) sixbit.getVal(9);
        this.utcSec = (int) sixbit.getVal(6);
        this.spare2 = (int) sixbit.getVal(2);

		this.spare2 = (int) sixbit.getVal(4);
		this.name = sixbit.getString(20);
		this.shipType = (int) sixbit.getVal(8);
		this.dimBow = (int) sixbit.getVal(9);
		this.dimStern = (int) sixbit.getVal(9);
		this.dimPort = (int) sixbit.getVal(6);
		this.dimStarboard = (int) sixbit.getVal(6);
		this.posType = (int) sixbit.getVal(4);
		this.raimFlag = (int) sixbit.getVal(1);
		this.dte = (int) sixbit.getVal(1);
		this.modeFlag = (int) sixbit.getVal(1);
		this.spare3 = (int) sixbit.getVal(4);
	}

	@Override
	public SixbitEncoder getEncoded() {
		SixbitEncoder encoder = super.encode();
		encoder.addVal(spare1, 8);
		encoder.addVal(sog, 10);
		encoder.addVal(posAcc, 1);
		encoder.addVal(pos.getRawLongitude(), 28);
		encoder.addVal(pos.getRawLatitude(), 27);
		encoder.addVal(cog, 12);
		encoder.addVal(trueHeading, 9);
		encoder.addVal(utcSec, 6);
		encoder.addVal(spare2, 4);
		encoder.addString(name, 20);
		encoder.addVal(shipType, 8);
		encoder.addVal(dimBow, 9);
		encoder.addVal(dimStern, 9);
		encoder.addVal(dimPort, 6);
		encoder.addVal(dimStarboard, 6);
		encoder.addVal(posType, 4);
		encoder.addVal(raimFlag, 1);
		encoder.addVal(dte, 1);
		encoder.addVal(modeFlag, 1);
		encoder.addVal(spare3, 4);
		return encoder;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
		builder.append(", spare1=");
		builder.append(spare1);
		builder.append(", sog=");
		builder.append(sog);
		builder.append(", posAcc=");
		builder.append(posAcc);
		builder.append(", pos=");
		builder.append(pos);
		builder.append(", cog=");
		builder.append(cog);
		builder.append(", trueHeading=");
		builder.append(trueHeading);
		builder.append(", utcSec=");
		builder.append(utcSec);
		builder.append(", spare2=");
		builder.append(spare2);
		builder.append(", name=");
		builder.append(name);
		builder.append(", shipType=");
		builder.append(shipType);
		builder.append(", dimBow=");
		builder.append(dimBow);
		builder.append(", dimStern=");
		builder.append(dimStern);
		builder.append(", dimPort=");
		builder.append(dimPort);
		builder.append(", dimStarboard=");
		builder.append(dimStarboard);
		builder.append(", posType=");
		builder.append(posType);
		builder.append(", raimFlag=");
		builder.append(raimFlag);
		builder.append(", dte=");
		builder.append(dte);
		builder.append(", modeFlag=");
		builder.append(modeFlag);
		builder.append(", spare3=");
		builder.append(spare3);
		builder.append("]");
		return builder.toString();
	}

	public int getSpare1() {
		return spare1;
	}

	public void setSpare1(int spare1) {
		this.spare1 = spare1;
	}

	public int getSog() {
		return sog;
	}

	public void setSog(int sog) {
		this.sog = sog;
	}

	public int getPosAcc() {
		return posAcc;
	}

	public void setPosAcc(int posAcc) {
		this.posAcc = posAcc;
	}

	public AisPosition getPos() {
		return pos;
	}

	public void setPos(AisPosition pos) {
		this.pos = pos;
	}

	public int getCog() {
		return cog;
	}

	public void setCog(int cog) {
		this.cog = cog;
	}

	public int getTrueHeading() {
		return trueHeading;
	}

	public void setTrueHeading(int trueHeading) {
		this.trueHeading = trueHeading;
	}

	public int getUtcSec() {
		return utcSec;
	}

	public void setUtcSec(int utcSec) {
		this.utcSec = utcSec;
	}

	public int getSpare2() {
		return spare2;
	}

	public void setSpare2(int spare2) {
		this.spare2 = spare2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int shipType) {
		this.shipType = shipType;
	}

	public int getDimBow() {
		return dimBow;
	}

	public void setDimBow(int dimBow) {
		this.dimBow = dimBow;
	}

	public int getDimStern() {
		return dimStern;
	}

	public void setDimStern(int dimStern) {
		this.dimStern = dimStern;
	}

	public int getDimPort() {
		return dimPort;
	}

	public void setDimPort(int dimPort) {
		this.dimPort = dimPort;
	}

	public int getDimStarboard() {
		return dimStarboard;
	}

	public void setDimStarboard(int dimStarboard) {
		this.dimStarboard = dimStarboard;
	}

	public int getPosType() {
		return posType;
	}

	public void setPosType(int posType) {
		this.posType = posType;
	}

	public int getRaimFlag() {
		return raimFlag;
	}

	public void setRaimFlag(int raimFlag) {
		this.raimFlag = raimFlag;
	}

	public int getDte() {
		return dte;
	}

	public void setDte(int dte) {
		this.dte = dte;
	}

	public int getModeFlag() {
		return modeFlag;
	}

	public void setModeFlag(int modeFlag) {
		this.modeFlag = modeFlag;
	}

	public int getSpare3() {
		return spare3;
	}

	public void setSpare3(int spare3) {
		this.spare3 = spare3;
	}
	
}
