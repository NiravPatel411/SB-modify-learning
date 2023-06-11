package sagan.site;


import java.util.Scanner;

class main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int nums = scanner.nextInt();
    }
    public int pivotIndex(int[] nums) {
        int j = nums.length - 1 ;
        int leftSum = 0;
        int rightSum = 0;
        for(int i=0 ; i<j ; i++){
            if(i < j/2){
                leftSum += nums[i];
            } else if(i > j/2) {
                rightSum += nums[i];
            }
        }

        int index = j/2;
        while (0 <= index && index <= j ){
            if(leftSum > rightSum){
                leftSum -= nums[index-1];
                rightSum +=  nums[index];
                index = index - 1;
            } else if(leftSum < rightSum){
                leftSum += nums[index];
                rightSum -=  nums[index+1];
                index = index + 1;
            }

            if(leftSum == rightSum){
                return index;
            }
        }

        return -1;
    }
}