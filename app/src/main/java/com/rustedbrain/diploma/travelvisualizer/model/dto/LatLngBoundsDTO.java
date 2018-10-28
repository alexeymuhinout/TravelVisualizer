package com.rustedbrain.diploma.travelvisualizer.model.dto;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.LatLngDTO;

import java.io.Serializable;

public final class LatLngBoundsDTO implements Serializable {

    public BoundsLatLngDTO southwest;
    public BoundsLatLngDTO northeast;

    public LatLngBoundsDTO() {
    }

    public LatLngBoundsDTO(BoundsLatLngDTO var1, BoundsLatLngDTO var2) {
        if (var1 == null) {
            throw new IllegalArgumentException("null southwest");
        } else if (var2 == null) {
            throw new IllegalArgumentException("null northeast");
        } else if (var2.getLatitude() < var1.getLatitude()) {
            throw new IllegalArgumentException(String.format("southern latitude exceeds northern latitude (%s > %s)", var1.getLatitude(), var2.getLatitude()));
        }

        this.southwest = var1;
        this.northeast = var2;
    }

    public LatLngBoundsDTO(LatLngBounds bounds) {
        this(new BoundsLatLngDTO(bounds.southwest), new BoundsLatLngDTO(bounds.northeast));
    }

    public static LatLngBoundsDTO.Builder builder() {
        return new LatLngBoundsDTO.Builder();
    }

    private static double zza(double var0, double var2) {
        return (var0 - var2 + 360.0D) % 360.0D;
    }

    private static double zzb(double var0, double var2) {
        return (var2 - var0 + 360.0D) % 360.0D;
    }

    public final boolean contains(BoundsLatLngDTO var1) {
        double var4 = var1.getLatitude();
        return this.southwest.getLatitude() <= var4 && var4 <= this.northeast.getLatitude() && this.zza(var1.getLongitude());
    }

    public final LatLngBoundsDTO including(BoundsLatLngDTO var1) {
        double var3 = Math.min(this.southwest.getLatitude(), var1.getLatitude());
        double var5 = Math.max(this.northeast.getLatitude(), var1.getLatitude());
        double var7 = this.northeast.getLongitude();
        double var9 = this.southwest.getLongitude();
        double var11 = var1.getLongitude();
        if (!this.zza(var11)) {
            if (zza(var9, var11) < zzb(var7, var11)) {
                var9 = var11;
            } else {
                var7 = var11;
            }
        }

        return new LatLngBoundsDTO(new BoundsLatLngDTO(var3, var9), new BoundsLatLngDTO(var5, var7));
    }

    public final BoundsLatLngDTO getCenter() {
        double var1 = (this.southwest.getLatitude() + this.northeast.getLatitude()) / 2.0D;
        double var3 = this.northeast.getLongitude();
        double var5 = this.southwest.getLongitude();
        double var7;
        if (this.southwest.getLongitude() <= var3) {
            var7 = (var3 + var5) / 2.0D;
        } else {
            var7 = (var3 + 360.0D + var5) / 2.0D;
        }

        return new BoundsLatLngDTO(var1, var7);
    }

    private final boolean zza(double var1) {
        if (this.southwest.getLongitude() <= this.northeast.getLongitude()) {
            return this.southwest.getLongitude() <= var1 && var1 <= this.northeast.getLongitude();
        } else {
            return this.southwest.getLongitude() <= var1 || var1 <= this.northeast.getLongitude();
        }
    }

    public final boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof LatLngBoundsDTO)) {
            return false;
        } else {
            LatLngBoundsDTO var2 = (LatLngBoundsDTO) var1;
            return this.southwest.equals(var2.southwest) && this.northeast.equals(var2.northeast);
        }
    }

    public static class BoundsLatLngDTO extends LatLngDTO {

        public BoundsLatLngDTO() {
        }

        public BoundsLatLngDTO(double var1, double var3) {
            if (-180.0D <= var3 && var3 < 180.0D) {
                this.longitude = var3;
            } else {
                this.longitude = ((var3 - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
            }

            this.latitude = Math.max(-90.0D, Math.min(90.0D, var1));
        }

        public BoundsLatLngDTO(LatLng latLng) {
            this(latLng.latitude, latLng.longitude);
        }

        public final int hashCode() {
            long var2 = Double.doubleToLongBits(this.latitude);
            int var1 = 31 + (int) (var2 ^ var2 >>> 32);
            var2 = Double.doubleToLongBits(this.longitude);
            return var1 * 31 + (int) (var2 ^ var2 >>> 32);
        }

        public final boolean equals(Object var1) {
            if (this == var1) {
                return true;
            } else if (!(var1 instanceof BoundsLatLngDTO)) {
                return false;
            } else {
                BoundsLatLngDTO var2 = (BoundsLatLngDTO) var1;
                return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(var2.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(var2.longitude);
            }
        }

        public final String toString() {
            return (new StringBuilder(60)).append("lat/lng: (").append(this.latitude).append(",").append(this.longitude).append(")").toString();
        }
    }

    public static final class Builder {
        private double zzdg = 1.0D / 0.0;
        private double zzdh = -1.0D / 0.0;
        private double zzdi = 0.0D / 0.0;
        private double zzdj = 0.0D / 0.0;

        public Builder() {
        }

        public final LatLngBoundsDTO.Builder include(BoundsLatLngDTO var1) {
            this.zzdg = Math.min(this.zzdg, var1.getLatitude());
            this.zzdh = Math.max(this.zzdh, var1.getLatitude());
            double var2 = var1.getLongitude();
            if (Double.isNaN(this.zzdi)) {
                this.zzdi = var2;
            } else {
                if (this.zzdi <= this.zzdj ? this.zzdi <= var2 && var2 <= this.zzdj : this.zzdi <= var2 || var2 <= this.zzdj) {
                    return this;
                }

                if (LatLngBoundsDTO.zza(this.zzdi, var2) < LatLngBoundsDTO.zzb(this.zzdj, var2)) {
                    this.zzdi = var2;
                    return this;
                }
            }

            this.zzdj = var2;
            return this;
        }

        public final LatLngBoundsDTO build() {
            if (Double.isNaN(this.zzdi)) {
                throw new IllegalArgumentException("no included points");
            }
            return new LatLngBoundsDTO(new BoundsLatLngDTO(this.zzdg, this.zzdi), new BoundsLatLngDTO(this.zzdh, this.zzdj));
        }
    }
}