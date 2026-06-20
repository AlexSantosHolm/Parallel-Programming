import numpy as np

# Data for k = 20 (replace with your actual measured values)
print("=" * 80)
print("TABLE 1: Execution Times for k = 20 (median of 7 runs)")
print("=" * 80)
print(f"{'n':<15} {'A1 (Arrays.sort)':<20} {'A2 (Sequential)':<20} {'A3 (Parallel)':<20}")
print(f"{'':15} {'(ms)':<20} {'(ms)':<20} {'(ms)':<20}")
print("-" * 80)

10000000
100000000
n_values_k20 = [1000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000]
a1_k20 = [0.23, 0.71, 6.56, 51.72, 617.07, 7125.21]  # Replace with your measurements
a2_k20 = [0.03, 0.18, 0.35, 1.00, 6.69, 162.83]   # Replace with your measurements
a3_k20 = [0.24, 0.35, 0.56, 1.39, 3.72, 34.09]          # Replace with your measurements

for n, a1, a2, a3 in zip(n_values_k20, a1_k20, a2_k20, a3_k20):
    print(f"{n:<15,} {a1:<20.2f} {a2:<20.2f} {a3:<20.2f}")

print("\n" + "=" * 80)
print("TABLE 2: Execution Times for k = 100 (median of 7 runs)")
print("=" * 80)
print(f"{'n':<15} {'A1 (Arrays.sort)':<20} {'A2 (Sequential)':<20} {'A3 (Parallel)':<20}")
print(f"{'':15} {'(ms)':<20} {'(ms)':<20} {'(ms)':<20}")
print("-" * 80)

n_values_k100 = [1000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000]
a1_k100 = [0.25, 0.70, 4.58, 52.10, 623.56, 7129.64]  # Replace with your measurements
a2_k100 = [0.26, 0.16, 1.84, 1.10, 7.04, 112.71]   # Replace with your measurements
a3_k100 = [0.44, 0.73, 1.18, 1.84, 3.44, 33.25]          # Replace with your measurements

for n, a1, a2, a3 in zip(n_values_k100, a1_k100, a2_k100, a3_k100):
    print(f"{n:<15,} {a1:<20.2f} {a2:<20.2f} {a3:<20.2f}")

print("\n" + "=" * 80)
print("SPEEDUP ANALYSIS")
print("=" * 80)

# Calculate speedups for k=20
print("\nSpeedup for k = 20 (A3 vs A2):")
print(f"{'n':<15} {'Speedup (A2/A3)':<20}")
print("-" * 35)
for n, a2, a3 in zip(n_values_k20, a2_k20, a3_k20):
    speedup = a2 / a3
    print(f"{n:<15,} {speedup:<20.2f}x")

# Calculate speedups for k=100
print("\nSpeedup for k = 100 (A3 vs A2):")
print(f"{'n':<15} {'Speedup (A2/A3)':<20}")
print("-" * 35)
for n, a2, a3 in zip(n_values_k100, a2_k100, a3_k100):
    speedup = a2 / a3
    print(f"{n:<15,} {speedup:<20.2f}x")

print("\n" + "=" * 80)