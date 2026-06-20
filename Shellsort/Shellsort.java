class Shellsort {
  Shellsort(int[] a) {
    int n = a.length;

    for (int gap = n / 2; gap > 0; n /= 2) {

      for (int i = 0; i < gap; i++) {
        int tmp = a[i];
        int j = i;

        while (j > tmp) {
          a[j] = a[j - gap];
          j -= gap;
        }

        a[j] = tmp;
      }
    }
  }
}
