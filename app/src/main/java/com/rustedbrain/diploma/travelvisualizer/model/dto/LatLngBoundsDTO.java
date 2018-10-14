package com.rustedbrain.diploma.travelvisualizer.model.dto;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;

public final class LatLngBoundsDTO implements Serializable {

    public LatLngDTO southwest;
    public LatLngDTO northeast;

    public LatLngBoundsDTO() {
    }

    public LatLngBoundsDTO(LatLngDTO var1, LatLngDTO var2) {
        if (var1 == null) {
            throw new IllegalArgumentException("null southwest");
        } else if (var2 == null) {
            throw new IllegalArgumentException("null northeast");
        } else if (var2.latitude < var1.latitude) {
            throw new IllegalArgumentException(String.format("southern latitude exceeds northern latitude (%s > %s)", var1.latitude, var2.latitude));
        }

        this.southwest = var1;
        this.northeast = var2;
    }

    public LatLngBoundsDTO(LatLngBounds bounds) {
        this(new LatLngDTO(bounds.southwest), new LatLngDTO(bounds.northeast));
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

    public final boolean contains(LatLngDTO var1) {
        double var4 = var1.latitude;
        return this.southwest.latitude <= var4 && var4 <= this.northeast.latitude && this.zza(var1.longitude);
    }

    public final LatLngBoundsDTO including(LatLngDTO var1) {
        double var3 = Math.min(this.southwest.latitude, var1.latitude);
        double var5 = Math.max(this.northeast.latitude, var1.latitude);
        double var7 = this.northeast.longitude;
        double var9 = this.southwest.longitude;
        double var11 = var1.longitude;
        if (!this.zza(var11)) {
            if (zza(var9, var11) < zzb(var7, var11)) {
                var9 = var11;
            } else {
                var7 = var11;
            }
        }

        return new LatLngBoundsDTO(new LatLngDTO(var3, var9), new LatLngDTO(var5, var7));
    }

    public final LatLngDTO getCenter() {
        double var1 = (this.southwest.latitude + this.northeast.latitude) / 2.0D;
        double var3 = this.northeast.longitude;
        double var5 = this.southwest.longitude;
        double var7;
        if (this.southwest.longitude <= var3) {
            var7 = (var3 + var5) / 2.0D;
        } else {
            var7 = (var3 + 360.0D + var5) / 2.0D;
        }

        return new LatLngDTO(var1, var7);
    }

    private final boolean zza(double var1) {
        if (this.southwest.longitude <= this.northeast.longitude) {
            return this.southwest.longitude <= var1 && var1 <= this.northeast.longitude;
        } else {
            return this.southwest.longitude <= var1 || var1 <= this.northeast.longitude;
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

    public static class LatLngDTO {

        public double latitude;
        public double longitude;

        public LatLngDTO() {
        }

        public LatLngDTO(double var1, double var3) {
            if (-180.0D <= var3 && var3 < 180.0D) {
                this.longitude = var3;
            } else {
                this.longitude = ((var3 - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
            }

            this.latitude = Math.max(-90.0D, Math.min(90.0D, var1));
        }

        public LatLngDTO(LatLng latLng) {
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
            } else if (!(var1 instanceof LatLngDTO)) {
                return false;
            } else {
                LatLngDTO var2 = (LatLngDTO) var1;
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

        public final LatLngBoundsDTO.Builder include(LatLngDTO var1) {
            this.zzdg = Math.min(this.zzdg, var1.latitude);
            this.zzdh = Math.max(this.zzdh, var1.latitude);
            double var2 = var1.longitude;
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
            return new LatLngBoundsDTO(new LatLngDTO(this.zzdg, this.zzdi), new LatLngDTO(this.zzdh, this.zzdj));
        }
    }
}